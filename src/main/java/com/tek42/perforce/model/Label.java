package com.tek42.perforce.model;

/**
 * Represents a Perforce Label.
 * 
 * @author Mike Wille
 */
public class Label extends AbstractViewsSupport {
	String name;
	String update;
	String access;
	String owner;
	String description;
	String options;
	String revision;

	public Label() {
		super();
		name = "";
		update = "";
		access = "";
		owner = "";
		description = "";
		options = "";
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the update
	 */
	public String getUpdate() {
		return update;
	}

	/**
	 * @param update
	 *            the update to set
	 */
	public void setUpdate(String update) {
		this.update = update;
	}

	/**
	 * @return the access
	 */
	public String getAccess() {
		return access;
	}

	/**
	 * @param access
	 *            the access to set
	 */
	public void setAccess(String access) {
		this.access = access;
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner
	 *            the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the options
	 */
	public String getOptions() {
		return options;
	}

	/**
	 * @param options
	 *            the options to set
	 */
	public void setOptions(String options) {
		this.options = options;
	}

	/**
	 * @return the revision
	 */
	public String getRevision() {
		return revision;
	}

	/**
	 * @param revision
	 *            the revision to set
	 */
	public void setRevision(String revision) {
		this.revision = revision;
	}

}
