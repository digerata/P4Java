package com.tek42.perforce.parse;

import java.io.Writer;
import java.util.Map;

import com.tek42.perforce.PerforceException;
import com.tek42.perforce.model.User;

/**
 * Responsible for building and saving user objects
 * 
 * @author Mike Wille
 */
public class UserBuilder extends AbstractFormBuilder<User> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tek42.perforce.parse.AbstractFormBuilder#buildForm(java.util.Map)
	 */
	@Override
	public User buildForm(Map<String, String> fields) throws PerforceException {
		User user = new User();
		user.setUsername(getField("User", fields));
		user.setEmail(getField("Email", fields));
		user.setFullName(getField("FullName", fields));
		user.setPassword(getField("Password", fields));
		user.setJobView(getField("JobView", fields));
		user.setReviews(getField("Review", fields));
		return user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tek42.perforce.parse.Builder#getBuildCmd(java.lang.String)
	 */
	public String[] getBuildCmd(String id) {
		return new String[] { "p4", "user", "-o", id };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tek42.perforce.parse.Builder#getSaveCmd()
	 */
	public String[] getSaveCmd() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tek42.perforce.parse.Builder#save(java.lang.Object, java.io.Writer)
	 */
	public void save(User obj, Writer writer) throws PerforceException {
		// TODO Auto-generated method stub
	}

}
