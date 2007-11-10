package com.tek42.perforce.model;

/**
 * Represents a user in perforce.
 * 
 * @author Mike Wille
 */
public class User {
	String username;
	String email;
	String fullName;
	String password;
	String jobView;
	String reviews;

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName
	 *            the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the jobView
	 */
	public String getJobView() {
		return jobView;
	}

	/**
	 * @param jobView
	 *            the jobView to set
	 */
	public void setJobView(String jobView) {
		this.jobView = jobView;
	}

	/**
	 * @return the reviews
	 */
	public String getReviews() {
		return reviews;
	}

	/**
	 * @param reviews
	 *            the reviews to set
	 */
	public void setReviews(String reviews) {
		this.reviews = reviews;
	}

}
