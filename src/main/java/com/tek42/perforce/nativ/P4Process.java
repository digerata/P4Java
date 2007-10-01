package com.tek42.perforce.nativ;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

/*
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
*/
import com.tek42.perforce.*;

/*
 * Copyright (c) 2001, Perforce Software, All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/**
 * Handles the execution of all perforce commands. This class can be used
 * directly, but the preferred use of this API is through the
 * {@link com.perforce.api.SourceControlObject SourceControlObject} subclasses.
 * <p>
 * <b>Example Usage:</b>
 * 
 * <pre>
 * String l;
 * Env env = new Env();
 * String[] cmd = { &quot;p4&quot;, &quot;branches&quot; };
 * try {
 * 	P4Process p = new P4Process(env);
 * 	p.exec(cmd);
 * 	while(null != (l = p.readLine())) {
 * 		// Parse the output.
 * 	}
 * 	p.close();
 * } catch(Exception ex) {
 * 	throw new PerforceException(ex.getMessage());
 * }
 * </pre>
 * 
 * @author <a href="mailto:david@markley.cc">David Markley</a>
 * @version $Date: 2002/01/15 $ $Revision: #3 $
 * @see Env
 * @see SourceControlObject
 * @see Thread
 */
public class P4Process {
	private P4JNI jni_proc = null;

	private boolean using_native = false;
	
	private Depot depot;

	private Runtime rt;

	private Process p;

	private BufferedReader in, err;

	private BufferedWriter out;

	private int exit_code = 0;

	//private final Logger logger = LoggerFactory.getLogger("perforce");

	private String P4_ERROR = null;

	private String[] new_cmd;

	private long threshold = 10000; // The default is 10 seconds;

	private boolean raw = false;

	/**
	 * Constructor that specifies the source control environment.
	 * 
	 * @param e
	 *            Depot to use.
	 */
	public P4Process(Depot d) {
		if(d == null)
			throw new IllegalArgumentException("Depot is required.");
		
		rt = Runtime.getRuntime();
		this.depot = d;
		this.threshold = depot.getServerTimeout();
	}

	/**
	 * Returns the exit code returned when the underlying process exits.
	 * 
	 * @return Typical UNIX style return code.
	 */
	public int getExitCode() {
		return exit_code;
	}

	/**
	 * In raw mode, the process will return the prefix added by the "-s" command
	 * line option. The default is false.
	 */
	public void setRawMode(boolean raw) {
		this.raw = raw;
	}

	/**
	 * Returns the status of raw mode for this process.
	 */
	public boolean getRawMode() {
		return this.raw;
	}

	/**
	 * Executes a p4 command. This uses the class environment information to
	 * execute the p4 command specified in the String array. This array contains
	 * all the command line arguments that will be specified for execution,
	 * including "p4" in the first position.
	 * 
	 * @param cmd
	 *            Array of command line arguments ("p4" must be first).
	 */
	public synchronized void exec(String[] cmd) throws IOException {
		String[] pre_cmds = new String[12];
		int i = 0;
		pre_cmds[i++] = cmd[0];
		pre_cmds[i++] = "-s";// Forces all commands to use stdout for message
								// reporting, no longer read stderr
		if(!depot.getPort().trim().equals("")) {
			pre_cmds[i++] = "-p";
			pre_cmds[i++] = depot.getPort();
		}
		if(!depot.getUser().trim().equals("")) {
			pre_cmds[i++] = "-u";
			pre_cmds[i++] = depot.getUser();
		}
		if(!depot.getClient().trim().equals("")) {
			pre_cmds[i++] = "-c";
			pre_cmds[i++] = depot.getClient();
		}
		if(!depot.getPassword().trim().equals("")) {
			pre_cmds[i++] = "-P";
			pre_cmds[i++] = depot.getPassword();
		}
		if(cmd[1].equals("-x")) {
			pre_cmds[i++] = "-x";
			pre_cmds[i++] = cmd[2];
		}
		new_cmd = new String[(i + cmd.length) - 1];
		for(int j = 0; j < (i + cmd.length) - 1; j++) {
			if(j < i) {
				new_cmd[j] = pre_cmds[j];
			} else {
				new_cmd[j] = cmd[(j - i) + 1];
			}
		}
		//logger.info("P4Process.exec: ", new_cmd);
		//System.out.println("P4Process.exec: " + new_cmd);
		/*
		System.out.print("P4Process.exec: ");
		for(String v : new_cmd) {
			System.out.print(v + " ");
		}
		System.out.println();
		*/
		if(P4JNI.isValid()) {
			System.out.println("Using native library");
			native_exec(new_cmd);
			using_native = true;
		} else {
			System.out.println("Using java library");
			pure_exec(new_cmd);
			using_native = false;
		}
	}

	/**
	 * Executes the command utilizing the P4API. This method will be used only
	 * if the supporting Java Native Interface library could be loaded.
	 */
	private synchronized void native_exec(String[] cmd) throws IOException {
		jni_proc = new P4JNI();
		// P4JNI tmp = new P4JNI();
		jni_proc.runCommand(jni_proc, cmd, depot);
		in = jni_proc.getReader();
		err = in;
		out = jni_proc.getWriter();
	}

	/**
	 * Executes the command through a system 'exec'. This method will be used
	 * only if the supporting Java Native Interface library could not be loaded.
	 */
	private synchronized void pure_exec(String[] cmd) throws IOException {
		if(depot.getExecutable() == null)
			throw new IllegalArgumentException("Depot does not have a valid P4 executable.  Please setup the depot correctly.");
		
		cmd[0] = depot.getExecutable();
		p = rt.exec(cmd, depot.getEnvp());
		InputStream is = p.getInputStream();
		//logger.debug("P4Process.exec().is: " + is);
		InputStreamReader isr = new InputStreamReader(is);
		//logger.debug("P4Process.exec().isr: " + isr);
		in = new BufferedReader(isr);
		InputStream es = p.getErrorStream();
		//logger.debug("P4Process.exec().es: " + es);
		InputStreamReader esr = new InputStreamReader(es);
		//logger.debug("P4Process.exec().esr: " + esr);
		err = new BufferedReader(esr);

		OutputStream os = p.getOutputStream();
		//logger.debug("P4Process.exec().os: " + os);
		OutputStreamWriter osw = new OutputStreamWriter(os);
		//logger.debug("P4Process.exec().osw: " + osw);
		out = new BufferedWriter(osw);
	}

	/**
	 * Writes <code>line</code> to the standard input of the process.
	 * 
	 * @param line
	 *            Line to be written.
	 */
	public synchronized void print(String line) throws IOException {
		out.write(line);
	}

	/**
	 * Writes <code>line</code> to the standard input of the process. A
	 * newline is appended to the output.
	 * 
	 * @param line
	 *            Line to be written.
	 */
	public synchronized void println(String line) throws IOException {
		out.write(line);
		out.newLine();
	}

	/**
	 * Flushes the output stream to the process.
	 */
	public synchronized void flush() throws IOException {
		out.flush();
	}

	/**
	 * Returns the next line from the process, or null if the command has
	 * completed its execution.
	 */
	public synchronized String readLine() {
		if(using_native && null != jni_proc && jni_proc.isPiped()) {
			return native_readLine();
		} else {
			return pure_readLine();
		}
	}

	/**
	 * Reads the next line from the process. This method will be used only if
	 * the supporting Java Native Interface library could be loaded.
	 */
	private synchronized String native_readLine() {
		try {
			return in.readLine();
		} catch(IOException ex) {
			return null;
		}
	}

	/**
	 * Reads the next line from the process. This method will be used only if
	 * the supporting Java Native Interface library could not be loaded.
	 */
	private synchronized String pure_readLine() {
		String line;
		long current, timeout = ((new Date()).getTime()) + threshold;

		if(null == p || null == in || null == err)
			return null;
		// Debug.verbose("P4Process.readLine()");
		try {
			for(;;) {
				if(null == p || null == in || null == err) {
					//logger.error("P4Process.readLine(): Something went null");
					System.out.println("P4Process.readline(): Something went null");
					return null;
				}

				current = (new Date()).getTime();
				if(current >= timeout) {
					//logger.error("P4Process.readLine(): Timeout");
					System.out.println("P4Process.readLine(): timeout");
					// If this was generating a new object from stdin, return an
					// empty string. Otherwise, return null.
					for(int i = 0; i < new_cmd.length; i++) {
						if(new_cmd[i].equals("-i"))
							return "";
					}
					return null;
				}

				// Debug.verbose("P4Process.readLine().in: "+in);
				try {
					/**
					 * If there's something coming in from stdin, return it. We
					 * assume that the p4 command was called with -s which sends
					 * all messages to standard out pre-pended with a string
					 * that indicates what kind of messsage it is error warning
					 * text info exit
					 */
					// Some errors still come in on Standard error
					while(err.ready()) {
						line = err.readLine();
						if(null != line) {
							addP4Error(line + "\n");
						}
					}

					if(in.ready()) {
						line = in.readLine();
						//logger.debug("From P4:" + line);
						if(line.startsWith("error")) {
							if(!line.trim().equals("") && (-1 == line.indexOf("up-to-date"))
									&& (-1 == line.indexOf("no file(s) to resolve"))) {
								addP4Error(line);
								//System.out.println("error: " +line);
							}
						} else if(line.startsWith("warning")) {
						} else if(line.startsWith("text")) {
						} else if(line.startsWith("info")) {
						} else if(line.startsWith("exit")) {
							int exit_code = new Integer(line.substring(line.indexOf(" ") + 1, line.length()))
									.intValue();
							if(0 == exit_code) {
								//logger.debug("P4 Exec Complete.");
							} else {
								//logger.error("P4 exited with an Error!");
								System.out.println("P4 exited with an error!");
							}
							return null;
						}
						if(!raw)
							line = line.substring(line.indexOf(":") + 1).trim();
						//logger.debug("P4Process.readLine(): " + line);
						return line;
					}
				} catch(NullPointerException ne) {
				}
				// If there's nothing on stdin or stderr, check to see if the
				// process has exited. If it has, return null.
				try {
					exit_code = p.exitValue();
					return null;
				} catch(IllegalThreadStateException ie) {
					//logger.debug("P4Process: Thread is not done yet.");
					//System.out.println("P4Process: thread is not done yet.");
				}
				// Sleep for a second, so this thread can't become a CPU hog.
				try {
					//logger.debug("P4Process: Sleeping...");
					//System.out.println("P4Process: Sleeping...");
					Thread.sleep(100); // Sleep for 1/10th of a second.
				} catch(InterruptedException ie) {
				}
			}
		} catch(IOException ex) {
			return null;
		}
	}

	/**
	 * Waits for the process to exit and closes out the process. This method
	 * should be called after the {@link #exec(java.lang.String[]) exec} method
	 * in order to close things down properly.
	 * 
	 * @param out
	 *            The stream to which any errors should be sent.
	 * @return The exit value of the underlying process.
	 */
	public synchronized int close() throws IOException {
		if(using_native && null != jni_proc && jni_proc.isPiped()) {
			native_close();
		} else {
			pure_close();
		}
		/*
		 * if (0 != exit_code) { throw new IOException("P4Process ERROR: p4 sync
		 * exited with error ("+ exit_code+")"); }
		 */
		if(null != P4_ERROR) {
			throw new IOException(P4_ERROR);
		}
		return exit_code;
	}

	/**
	 * Closes down connections to the underlying process. This method will be
	 * used only if the supporting Java Native Interface library could be
	 * loaded.
	 */
	private synchronized void native_close() {
		try {
			in.close();
			out.flush();
			out.close();
		} catch(IOException ioe) {
		}
	}

	/**
	 * Closes down connections to the underlying process. This method will be
	 * used only if the supporting Java Native Interface library could not be
	 * loaded.
	 */
	private synchronized void pure_close() {
		/*
		 * Try to close this process for at least 30 seconds.
		 */
		for(int i = 0; i < 30; i++) {
			try {
				in.close();
				err.close();
				out.flush();
				out.close();
			} catch(IOException ioe) {
			}
			try {
				exit_code = p.waitFor();
				p.destroy();
				break;
			} catch(InterruptedException ie) {
			}
			try {
				Thread.sleep(1000);
			} catch(InterruptedException ie) {
			}
		}
	}

	/** Set the server timeout threshold. */
	public void setServerTimeout(long threshold) {
		this.threshold = threshold;
	}

	/** Return the server timeout threshold. */
	public long getServerTimeout() {
		return threshold;
	}

	private void addP4Error(String message) {
		if(null == P4_ERROR) {
			P4_ERROR = message;
		} else {
			P4_ERROR += message;
		}
	}
}
