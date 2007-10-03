package com.tek42.perforce.parse;

import com.tek42.perforce.*;
import com.tek42.perforce.model.User;

/**
 * Base API object for interacting with users
 * 
 * @author Mike Wille
 *
 */
public class Users extends AbstractPerforceTemplate {
	public Users(Depot depot) {
		super(depot);
	}
	
	/**
	 * Returns the user specified by username.
	 *
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public User getUser(String name) throws Exception {
		UserBuilder builder = new UserBuilder();
		User user = builder.build(getPerforceResponse(builder.getBuildCmd(name)));
		return user;
	}
}
