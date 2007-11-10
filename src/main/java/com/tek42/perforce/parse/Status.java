package com.tek42.perforce.parse;

import com.tek42.perforce.Depot;
import com.tek42.perforce.PerforceException;

/**
 * Allows checking the status of the depot.
 * 
 * @author Mike Wille
 */
public class Status extends AbstractPerforceTemplate {
	public Status(Depot depot) {
		super(depot);
	}

	/**
	 * Checks the environment to see if it is valid. To check the validity of the environment, the user information is
	 * accessed. This ensures that the server can be contacted and that the password is set properly.
	 * <p>
	 * If the environment is valid, this method will return true. Otherwise, it will throw a
	 * <code>PerforceException</code> with a message regarding the failure.
	 */
	public boolean isValid() throws PerforceException {
		getPerforceResponse(new String[] { "p4", "user", "-o" });
		return true;
	}

	/**
	 * Checks the specified path to see if it exists in the depot. This may take a bit of time the first time it is
	 * called. It seems perforce takes a bit to wake up.
	 * <p>
	 * The path must end with the perforce wildcard: /... Otherwise it will return no results.
	 * <p>
	 * Note: this method may move once the API is more complete.
	 * 
	 * @param path
	 *            Path to check, example: //depot/MyProject/...
	 * @return True if it exists, false if not.
	 * @throws PerforceException
	 */
	public boolean exists(String path) throws PerforceException {
		StringBuilder sb = getPerforceResponse(new String[] { "p4", "fstat", "-m", "1", path });
		if(sb.indexOf("no such file(s).") > 0)
			return false;
		return true;
	}
}
