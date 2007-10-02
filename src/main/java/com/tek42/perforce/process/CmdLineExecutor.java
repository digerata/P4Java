package com.tek42.perforce.process;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tek42.perforce.PerforceException;

/**
 * Executes on the command line.  This is not thread safe.
 * 
 * @author Mike Wille
 *
 */
public class CmdLineExecutor implements Executor {
	ProcessBuilder builder;
	Process currentProcess;
	List<String> args;
	BufferedWriter writer;
	BufferedReader reader;
	private final Logger logger = LoggerFactory.getLogger("perforce");
	
	/**
	 * Requires a map of environment variables (P4USER, P4CLIENT, P4PORT, etc)
	 * 
	 * @param environment
	 */
	public CmdLineExecutor(Map<String, String> environment) {
		args = new ArrayList<String>();
		builder = new ProcessBuilder(args);
		Map<String, String> env = builder.environment();
		for(String key : environment.keySet()) {
			env.put(key, environment.get(key));
		}
	}
	
	/* (non-Javadoc)
	 * @see com.tek42.perforce.process.P4Executor#exec(java.lang.String[])
	 */
	public void exec(String[] args) throws PerforceException {
		this.args.clear();
		String debug = "";
		for(String arg : args) {
			debug += arg + " ";
			this.args.add(arg);
		}
		logger.info("Executing: " + debug);
		builder.redirectErrorStream(true);
		try {
			currentProcess = builder.start();
			reader = new BufferedReader(new InputStreamReader(currentProcess.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(currentProcess.getOutputStream()));
			
		} catch(IOException e) {
			throw new PerforceException("Failed to open connection to: " + args[0], e);
		}
	}

	/* (non-Javadoc)
	 * @see com.tek42.perforce.process.P4Executor#getReader()
	 */
	public BufferedReader getReader() {
		return reader;
	}

	/* (non-Javadoc)
	 * @see com.tek42.perforce.process.P4Executor#getWriter()
	 */
	public BufferedWriter getWriter() {
		return writer;
	}
	
	
	/* (non-Javadoc)
	 * @see com.tek42.perforce.process.P4Executor#close()
	 */
	public void close() {
		try {
			if(reader != null) {
				reader.close();
			}
			reader = null;
		} catch(IOException e) { }
		
		try {
			if(writer != null) {
				writer.close();
			}
			writer = null;
		} catch(IOException e) { }
	}

	/**
	 * Useful for things like process.waitFor(). 
	 *
	 * @return
	 */
	public Process getProcess() {
		return currentProcess;
	}

}
