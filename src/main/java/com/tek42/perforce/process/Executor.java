package com.tek42.perforce.process;

import java.io.*;

import com.tek42.perforce.*;

/**
 * A simplified interface for interacting with another process.
 * 
 * @author Mike Wille
 *
 */
public interface Executor {
	/***
	 * Execute the specified command and its arguments
	 *
	 * @param args
	 * @throws PerforceException
	 */
	public void exec(String args[]) throws PerforceException;
	/**
	 * Returns a BufferedWriter for writing to the stdin of this process
	 *
	 * @return
	 */
	public BufferedWriter getWriter();
	/**
	 * Returns a BufferedReader for reading from the stdout/stderr of this
	 * process
	 *
	 * @return
	 */
	public BufferedReader getReader();
	/**
	 * Close down all open resources
	 */
	public void close();
}
