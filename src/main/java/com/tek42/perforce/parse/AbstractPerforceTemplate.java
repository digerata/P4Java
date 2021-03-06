/*
 *	P4Java - java integration with Perforce SCM
 *	Copyright (C) 2007-,  Mike Wille, Tek42
 *
 *	This library is free software; you can redistribute it and/or
 *	modify it under the terms of the GNU Lesser General Public
 *	License as published by the Free Software Foundation; either
 *	version 2.1 of the License, or (at your option) any later version.
 *
 *	This library is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *	Lesser General Public License for more details.
 *
 *	You should have received a copy of the GNU Lesser General Public
 *	License along with this library; if not, write to the Free Software
 *	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *	You can contact the author at:
 *
 *	Web:	http://tek42.com
 *	Email:	mike@tek42.com
 *	Mail:	755 W Big Beaver Road
 *			Suite 1110
 *			Troy, MI 48084
 */

package com.tek42.perforce.parse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;

import com.tek42.perforce.Depot;
import com.tek42.perforce.PerforceException;
import com.tek42.perforce.process.Executor;

/**
 * Provides default functionality for interacting with Perforce using the template design pattern.
 * 
 * @author Mike Wille
 */
public abstract class AbstractPerforceTemplate {
	Depot depot;
	Logger logger;
    final String errors[] = new String[] {
        "Connect to server failed; check $P4PORT",
		"Perforce password (P4PASSWD) invalid or unset.",
		"Password not allowed at this server security level, use 'p4 login'",
		"Can't create a new user - over license quota.",
		"Client '*' can only be used from host '*'",
		"Access for user '",
		"Your session has expired, please login again."
	};

	final String maxError = "Request too large";

	public AbstractPerforceTemplate(Depot depot) {
		this.depot = depot;
		this.logger = depot.getLogger();
	}

	/**
	 * Parses lines of formatted text for a list of values. Tokenizes each line into columns and adds the column
	 * specified by index to the list.
	 * 
	 * @param response	The response from perforce to parse
	 * @param index		The column index to add to the list
	 * @return	A List of strings parsed from the response
	 */
	protected List<String> parseList(StringBuilder response, int index) {
		StringTokenizer lines = new StringTokenizer(response.toString(), "\n\r");
		List<String> list = new ArrayList<String>(100);
		while(lines.hasMoreElements()) {
			StringTokenizer columns = new StringTokenizer(lines.nextToken());
			for(int column = 0; column < index; column++) {
				columns.nextToken();
			}
			list.add(columns.nextToken());
		}
		return list;

	}

	/**
	 * Check to see if the perforce request resulted in a "too many results" error.  If so, special handling needs
	 * to happen.
	 *
	 * @param response The response from perforce
	 * @return	True if the limit was reached, false otherwise.
	 */
	protected boolean hitMax(StringBuilder response) {
		return response.toString().startsWith(maxError);
	}

	/**
	 * Adds any extra parameters that need to be applied to all perforce commands. For example, adding the login ticket
	 * to authenticate with.
	 * 
	 * @param cmd
	 *            String array that will be executed
	 * @return A (possibly) modified string array to be executed in place of the original.
	 */
	protected String[] getExtraParams(String cmd[]) {
		String ticket = depot.getP4Ticket();

		if(ticket != null) {
			// Insert the ticket for the password if tickets are being used...
			String newCmds[] = new String[cmd.length + 2];
			newCmds[0] = "p4";
			newCmds[1] = "-P";
			newCmds[2] = ticket;
			for(int i = 3; (i - 2) < cmd.length; i++) {
				newCmds[i] = cmd[i - 2];
			}
			cmd = newCmds;
		}
		return cmd;
	}

	/**
	 * Handles the IO for opening a process, writing to it, flushing, closing, and then handling any errors.
	 * 
	 * @param object	The perforce object to save
	 * @param builder	The builder responsible for saving the object
	 * @throws PerforceException	If there is any errors thrown from perforce
	 */
	@SuppressWarnings("unchecked")
	protected StringBuilder saveToPerforce(Object object, Builder builder) throws PerforceException {
		boolean loop = false;
		boolean attemptLogin = true;

		StringBuilder response = new StringBuilder();
		do {
			int mesgIndex = -1, i;//, count = 0;
			Executor p4 = depot.getExecFactory().newExecutor();
			String debugCmd = "";
			try {
				String cmds[] = getExtraParams(builder.getSaveCmd(object));

				// for exception reporting...
				for(String cm : cmds) {
					debugCmd += cm + " ";
				}

				// back to our regularly scheduled programming...
				p4.exec(cmds);
				BufferedReader reader = p4.getReader();

				// Maintain a log of what was sent to p4 on std input
				final StringBuilder log = new StringBuilder();

				// Conditional use of std input for saving the perforce entity
				if(builder.requiresStandardInput()) {
					BufferedWriter writer = p4.getWriter();
					Writer fwriter = new FilterWriter(writer) {
						public void write(String str) throws IOException {
							log.append(str);
							out.write(str);
						}
					};
					builder.save(object, fwriter);
					fwriter.flush();
					fwriter.close();
				}

				String line;
				String error = "";
				int exitCode = 0;

				while((line = reader.readLine()) != null) {
					logger.debug("LineIn -> " + line);

					// Check for authentication errors...
					for(i = 0; i < errors.length; i++) {
						if(line.indexOf(errors[i]) != -1)
							mesgIndex = i;

					}

					if(line.startsWith("error")) {
						if(!line.trim().equals("") && (line.indexOf("up-to-date") < 0) && (line.indexOf("no file(s) to resolve") < 0)) {
							error += line.substring(6);
						}

					} else if(line.startsWith("exit")) {
						exitCode = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));

					} else {
						if(line.indexOf(":") > -1)
							response.append(line.substring(line.indexOf(":")));
						else
							response.append(line);
					}
				}
				reader.close();

				loop = false;
				// If we failed to execute because of an authentication issue, try a p4 login.
				if(attemptLogin && (mesgIndex == 1 || mesgIndex == 2 || mesgIndex == 6)) {
					// password is unset means that perforce isn't using the environment var P4PASSWD
					// Instead it is using tickets. We must attempt to login via p4 login, then
					// retry this cmd.
					p4.close();
					login();
					loop = true;
					attemptLogin = false;
					continue;
				}
				
				if(exitCode != 0) {
					if(!error.equals(""))
						throw new PerforceException(error + "\nFor Command: " + debugCmd + "\nWith Data:\n===================\n" + log.toString() + "===================\n");
					throw new PerforceException(response.toString());
				}

				logger.debug("Wrote to " + debugCmd + ":\n" + log.toString());
				logger.info(response.toString());

			} catch(IOException e) {
				throw new PerforceException("Failed to open connection to perforce", e);
			} finally {
				p4.close();
			}
		} while(loop);
		
		return response;
	}

	/**
	 * Executes a perforce command and returns the output as a StringBuilder.
	 * 
	 * @param cmd	The perforce commands to execute.  Each command and argument is it's own array element
	 * @return	The response from perforce as a stringbuilder
	 * @throws PerforceException	If perforce throws any errors
	 */
	protected StringBuilder getPerforceResponse(String cmd[]) throws PerforceException {
		// TODO: Create a way to wildcard portions of the error checking.  Add method to check for these errors.
		boolean loop = false;
		boolean attemptLogin = true;

		StringBuilder response = new StringBuilder();
		do {
			int mesgIndex = -1, i, count = 0;
			Executor p4 = depot.getExecFactory().newExecutor();
			String debugCmd = "";
			try {
				// get entire cmd to execute
				cmd = getExtraParams(cmd);

				// setup information for logging...
				for(String cm : cmd) {
					debugCmd += cm + " ";
				}

				// Performe execution and IO
				p4.exec(cmd);
				BufferedReader reader = p4.getReader();
				String line;
				response = new StringBuilder();
				while((line = reader.readLine()) != null) {
					count++;
					for(i = 0; i < errors.length; i++) {
						if(line.indexOf(errors[i]) != -1)
							mesgIndex = i;
					}
					response.append(line);
					response.append("\n");
				}
				loop = false;
				// If we failed to execute because of an authentication issue, try a p4 login.
				if(attemptLogin && (mesgIndex == 1 || mesgIndex == 2 || mesgIndex == 6)) {
					// password is unset means that perforce isn't using the environment var P4PASSWD
					// Instead it is using tickets. We must attempt to login via p4 login, then
					// retry this cmd.
					p4.close();
					login();
					loop = true;
					attemptLogin = false;
					continue;
				}

				// We aren't using the exact message because we want to add the username for more info
				if(mesgIndex == 4)
					throw new PerforceException("Access for user '" + depot.getUser() + "' has not been enabled by 'p4 protect'");
				if(mesgIndex != -1)
					throw new PerforceException(errors[mesgIndex]);
				if(count == 0)
					throw new PerforceException("No output for: " + debugCmd);

			} catch(IOException e) {
				throw new PerforceException("Failed to communicate with p4", e);
			} finally {
				p4.close();
			}
		} while(loop);

		return response;
	}

	/**
	 * Tries to perform a p4 login if the security level on the server is set to level 3 and no ticket was set via
	 * depot.setP4Ticket().
	 * <p>
	 * Unfortunately, this likely doesn't work on windows.
	 * 
	 * @throws PerforceException	If perforce throws any errors
	 */
	protected void login() throws PerforceException {
		// Unfortunately, the simple way of doing this: echo password | p4 login
		// Doesn't work on windows! So we have to try and write directly, but
		// that doesn't seem to work either. The code is left here, but probably is
		// not going to work. If you are facing this problem, use depot.setTicket() with a ticket
		// that has an expiration significantly far ahead in time to work as a permanent login.
		String sep = System.getProperty("file.separator");
		if(sep.equals("\\")) {
			Executor login = depot.getExecFactory().newExecutor();
			login.exec(new String[] { "p4", "login" });
			try {
				Thread.sleep(250);
			} catch(InterruptedException e) {
				// nothing to do
			}
			try {
				login.getWriter().write(depot.getPassword() + "\n");
			} catch(IOException e) {
				throw new PerforceException("Failed to communicate with p4 when logging in to server.");
			}
			login.close();
		} else { // for everything not windows...
			Executor login = depot.getExecFactory().newExecutor();
			// The -p parameter outputs the ticket to stdout.
			final String[] args = {"/bin/sh", "-c", depot.getExecutable() + " login -p"};
			logger.info("Running " + Arrays.toString(args));
			login.exec(null, args);
			String ticket = null;
			try {
				login.getWriter().write(depot.getPassword()==null ? "" : depot.getPassword());
				login.getWriter().newLine();
				login.getWriter().flush();
				BufferedReader reader = login.getReader();
				String line;
				
				// The last line output from p4 login will be the ticket
				while((line = reader.readLine()) != null) {
					ticket = line;
				}
			
			} catch(IOException e) {
				throw new PerforceException("Unable to login via p4 login due to IOException: " + e.getMessage());
			}
			// if we obtained a ticket, save it for later use. Our environment setup by Depot can't usually
			// see the .p4tickets file.
			if(ticket != null) {
				ticket = ticket.trim();
				if(ticket.contains(" ")) {
					throw new PerforceException("Failed to login: " + ticket);
				}
				logger.warn("Using p4 issued ticket.");
				depot.setP4Ticket(ticket);
			}

			login.close();
		}
	}
}
