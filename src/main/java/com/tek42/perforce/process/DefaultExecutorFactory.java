package com.tek42.perforce.process;

import java.util.Map;
import java.util.HashMap;

/**
 * @{link ExecutorFactory} that handles creating new {@link CmdLineExecutors}
 * 
 * @author Mike Wille
 *
 */
public class DefaultExecutorFactory implements ExecutorFactory {
	Map<String, String> env;
	
	/* (non-Javadoc)
	 * @see com.tek42.perforce.process.ExecutorFactory#newExecutor()
	 */
	public Executor newExecutor() {
		return new CmdLineExecutor(env);
	}

	/* (non-Javadoc)
	 * @see com.tek42.perforce.process.ExecutorFactory#setEnv(java.util.Map)
	 */
	public void setEnv(Map<String, String> env) {
		this.env = new HashMap<String, String>();
		for(String key : env.keySet())
			this.env.put(key, env.get(key));
	}
}
