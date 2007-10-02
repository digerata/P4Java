package com.tek42.perforce.process;

import java.util.*;

/**
 * Parent interface to handle creation of new {@link Executor} objects.
 * 
 * @author Mike Wille
 *
 */
public interface ExecutorFactory {
	public void setEnv(Map<String, String> env);
	public Executor newExecutor();
}
