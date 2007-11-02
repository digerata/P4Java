package com.tek42.perforce.process;

import java.util.*;

/**
 * Parent interface to handle creation of new {@link Executor} objects.
 * 
 * @author Mike Wille
 *
 */
public interface ExecutorFactory {
	/**
	 * Sets a the environment variables for processes to be run. 
	 *
	 * @param env
	 */
	public void setEnv(Map<String, String> env);
	/**
	 * Creates a new executor for running a process.
	 *
	 * @return
	 */
	public Executor newExecutor();
}
