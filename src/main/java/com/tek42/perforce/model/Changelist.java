package com.tek42.perforce.model;

import java.util.List;

/**
 * Represents a changelist in Perforce.
 * <p>
 * Again Perforce fails us with an imcomplete API.  Their change object does not
 * contain a record of files or jobs attached to the change. Grr...  I'm forced to
 * create one that is more complete.
 * <p>
 * This class maps the output of p4 describe [ChangeNumber].  However, it does not
 * contain the diffs ouput by that command.
 * 
 * @author Mike Wille
 *
 */
public class Changelist {
	int changeNumber;
	String workspace;
	String date;
	String user;
	String description;
	List<FileEntry> files;
	List<JobEntry> jobs;
	
	/**
	 * Perforce has multiple files per change.  This class represents
	 * a single file within a change which includes the action, filename, and revision.
	 *
	 * @author Mike Wille
	 *
	 */
	public static class FileEntry {
		public static enum Action { ADD, CHANGE, DELETE, BRANCH };
		Action action;
		String filename;
		String revision;
		
		/**
		 * @return the action
		 */
		public Action getAction() {
			return action;
		}
		/**
		 * @param action the action to set
		 */
		public void setAction(Action action) {
			this.action = action;
		}
		/**
		 * @return the filename
		 */
		public String getFilename() {
			return filename;
		}
		/**
		 * @param filename the filename to set
		 */
		public void setFilename(String filename) {
			this.filename = filename;
		}
		/**
		 * @return the revision
		 */
		public String getRevision() {
			return revision;
		}
		/**
		 * @param revision the revision to set
		 */
		public void setRevision(String revision) {
			this.revision = revision;
		}
	}
	
	/**
	 * Perforce links issues to changes via jobs.  This represents a job attached to
	 * a change.
	 *
	 * @author Mike Wille
	 *
	 */
	public static class JobEntry {
		public static enum Status { OPEN, CLOSED };
		Status status;
		String job;
		String description;
		/**
		 * @return the status
		 */
		public Status getStatus() {
			return status;
		}
		/**
		 * @param status the status to set
		 */
		public void setStatus(Status status) {
			this.status = status;
		}
		/**
		 * @return the job
		 */
		public String getJob() {
			return job;
		}
		/**
		 * @param job the job to set
		 */
		public void setJob(String job) {
			this.job = job;
		}
		/**
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}
		/**
		 * @param description the description to set
		 */
		public void setDescription(String description) {
			this.description = description;
		}
	}

	/**
	 * @return the changeNumber
	 */
	public int getChangeNumber() {
		return changeNumber;
	}

	/**
	 * @param changeNumber the changeNumber to set
	 */
	public void setChangeNumber(int changeNumber) {
		this.changeNumber = changeNumber;
	}

	/**
	 * @return the workspace
	 */
	public String getWorkspace() {
		return workspace;
	}

	/**
	 * @param workspace the workspace to set
	 */
	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the files
	 */
	public List<FileEntry> getFiles() {
		return files;
	}

	/**
	 * @param files the files to set
	 */
	public void setFiles(List<FileEntry> files) {
		this.files = files;
	}

	/**
	 * @return the jobs
	 */
	public List<JobEntry> getJobs() {
		return jobs;
	}

	/**
	 * @param jobs the jobs to set
	 */
	public void setJobs(List<JobEntry> jobs) {
		this.jobs = jobs;
	}
}
