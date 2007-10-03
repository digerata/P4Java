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

import com.tek42.perforce.PerforceException;
import com.tek42.perforce.process.Executor;
import com.tek42.perforce.Depot;

/**
 * Provides default functionality for interacting with perforce.
 * 
 * @author mwille
 *
 */
public abstract class AbstractPerforceTemplate {
	Depot depot;
	Logger logger;
	
	public AbstractPerforceTemplate(Depot depot) {
		this.depot = depot;
		this.logger = depot.getLogger();
	}
	
	/**
	 * Parses lines of formatted text for a list of values.  Tokenizes each line into columns
	 * and adds the column specified by index to the list.
	 *
	 * @param response
	 * @param index
	 * @return
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
	 * Handles the IO for opening a process, writing to it, flushing, closing, and then handling
	 * any errors.
	 *
	 * @param object
	 * @param builder
	 * @throws PerforceException
	 */
	@SuppressWarnings("unchecked")
	protected void saveToPerforce(Object object, Builder builder) throws PerforceException {
		Executor p4 = depot.getExecFactory().newExecutor();
		try {
			// for exception reporting...
			String cmds[] = builder.getSaveCmd();
			String cmd = "";
			for(String cm : cmds) {
				cmd += cm + " ";
			}
			// back to our regularly scheduled programming...
			p4.exec(builder.getSaveCmd());
			BufferedReader reader = p4.getReader();
			
			BufferedWriter writer = p4.getWriter();
			final StringBuilder log = new StringBuilder();
			Writer fwriter = new FilterWriter(writer) {
				public void write(String str) throws IOException {
					log.append(str);
					out.write(str);
				}
			};
			builder.save(object, fwriter);
			fwriter.flush();
			fwriter.close();
			
			String line;
			String error = "";
			String info = "";
			int exitCode = 0;
			while((line = reader.readLine()) != null) {
				logger.debug("LineIn -> " + line);
				if(line.startsWith("error")) {
					if(!line.trim().equals("") 
							&& (line.indexOf("up-to-date") < 0)
							&& (line.indexOf("no file(s) to resolve") < 0)) {
						error += line.substring(6);
					}
				
				} else if(line.startsWith("exit")){
					exitCode = new Integer(line.substring(line.indexOf(" ") + 1, line.length())).intValue();
					
				} else {
					if(line.indexOf(":") > -1)
						info += line.substring(line.indexOf(":"));
					else
						info += line;
				}
			}
			reader.close();
			
			if(exitCode != 0) {
				if(!error.equals(""))
					throw new PerforceException(error + "\nFor Command: " + cmd + "\nWith Data:\n===================\n" + log.toString() + "===================\n");
				throw new PerforceException(info); 
			}
			
			logger.info(info);
		
		} catch(IOException e) {
			throw new PerforceException("Failed to open connection to perforce", e);
		} finally {
			p4.close();
		}
	}
	/**
	 * Executes a perforce command and returns the output as a StringBuilder.
	 *
	 * @param cmd
	 * @return
	 * @throws PerforceException
	 */
	protected StringBuilder getPerforceResponse(String cmd[]) throws PerforceException {
		String[] mesg = { "Connect to server failed; check $P4PORT", 
				"Perforce password (P4PASSWD) invalid or unset.",
				"Can't create a new user - over license quota." };
		int mesgIndex = -1, i, count = 0;
		Executor p4 = depot.getExecFactory().newExecutor();
		String debugcmd = "";
		try {
			for(String cm : cmd) {
				debugcmd += cm + " ";
			}
			p4.exec(cmd);
			
			BufferedReader reader = p4.getReader();
			String line;
			StringBuilder response = new StringBuilder();
			while((line = reader.readLine()) != null) {
				count++;
				for(i = 0; i < mesg.length; i++) {
					if(line.indexOf(mesg[i]) != -1)
						mesgIndex = i;
				}
				response.append(line + "\n");
			}
			if(mesgIndex != -1)
				throw new PerforceException(mesg[mesgIndex]);
			if(count == 0)
				throw new PerforceException("No output for: " + debugcmd);
			
			return response;
		} catch(IOException e) {
			throw new PerforceException("Failed to communicate with p4", e);
		} finally {
			p4.close();
		}
	}
}
