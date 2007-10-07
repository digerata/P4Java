package com.tek42.perforce.parse;

import com.tek42.perforce.Depot;
import com.tek42.perforce.PerforceException;

/**
 * Allows checking the status of the depot
 * 
 * @author Mike Wille
 */
public class Status extends AbstractPerforceTemplate {
	public Status(Depot depot) {
		super(depot);
	}
	
	/**
	 * Checks the environment to see if it is valid. To check the validity of
	 * the environment, the user information is accessed. This ensures that the
	 * server can be contacted and that the password is set properly.
	 * <p>
	 * If the environment is valid, this method will return true. Otherwise,
	 * it will throw a <code>PerforceException</code> with a message regarding
	 * the failure.
	 */
	public boolean isValid() throws PerforceException {
		StringBuilder sb = getPerforceResponse(new String[] { "p4", "user", "-o" });
		return true;
	}
}
