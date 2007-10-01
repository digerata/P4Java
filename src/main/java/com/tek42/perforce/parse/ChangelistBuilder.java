package com.tek42.perforce.parse;

import java.io.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tek42.perforce.PerforceException;
import com.tek42.perforce.model.Changelist;

/**
 * Builder for changelists
 * 
 * @author Michael Wille
 *
 */
public class ChangelistBuilder implements Builder<Changelist> {
	private final Logger logger = LoggerFactory.getLogger("perforce");
	
	public String[] getBuildCmd(String id) {
		return new String[] { "p4", "describe", "-s", id };
	}
	
	/* (non-Javadoc)
	 * @see com.tek42.perforce.parse.Builder#build(java.lang.StringBuilder)
	 */
	public Changelist build(StringBuilder sb) throws PerforceException {
		Changelist change = null;
		StringTokenizer lines = new StringTokenizer(sb.toString(), "\t\n\r");
		try {
			while(lines.hasMoreElements()) {
				String line = lines.nextToken();
				logger.debug("Line: " + line);
				
				if(line.startsWith("Change")) {
					logger.debug("New changelist.");
					
					change = new Changelist();
					// Line looks like:
					// Change XXXX by user@client on YYYY/MM/DD HH:MM:SS
					StringTokenizer details = new StringTokenizer(line);
					details.nextToken(); // client
					change.setChangeNumber(new Integer(details.nextToken()));
					details.nextToken(); // by
					String user = details.nextToken();
					change.setUser(user.substring(0, user.indexOf("@")));
					change.setWorkspace(user.substring(user.indexOf("@") + 1));
					details.nextToken(); // on
					change.setDate(details.nextToken());
					
					// the lines immediately following is the description
					String desc = "";
					line = lines.nextToken();
					while(line != null && !line.startsWith("Affected files") && !line.startsWith("Jobs fixed")) {
						logger.debug("Description Line: " + line);
						desc += line + "\n";
						line = lines.nextToken();
					} 
					change.setDescription(desc.trim());
					
				}
				 
				if(line.startsWith("Jobs fixed")) {
					logger.debug("Has jobs.");
					List<Changelist.JobEntry> jobs = new ArrayList<Changelist.JobEntry>();
					boolean getDesc = false;
					Changelist.JobEntry job = new Changelist.JobEntry();
					String description = null;
					do {
						line = lines.nextToken();
						logger.debug("Job Line: "+ line);
						if(!getDesc) {
							// Line looks like:
							// EXT-84 on 2007/09/25 by mwille *closed*
							StringTokenizer details = new StringTokenizer(line);
							job = new Changelist.JobEntry();
							job.setJob(details.nextToken());
							details.nextToken(); // on
							details.nextToken(); // date
							details.nextToken(); // by
							details.nextToken(); // user
							job.setStatus(details.nextToken());
							description = "";
							getDesc = true;
						} else {
							while(!line.startsWith("Affected files")) {
								description += line;
								if(!lines.hasMoreElements())
									break;
								description += "\n";
								line = lines.nextToken();
							}
							job.setDescription(description.trim());
							jobs.add(job);
							getDesc = false;
						}
						
					} while(!line.startsWith("Affected files"));
					
					change.setJobs(jobs);
					
				}
				
				if(line.startsWith("Affected files")) {
					logger.debug("reading files...");
					List<Changelist.FileEntry> files = new ArrayList<Changelist.FileEntry>();
					
					while(lines.hasMoreElements()) {
						String entry = lines.nextToken();
						logger.debug("File Line: " + entry);
						//if(!entry.startsWith("..."))
						//	break;
						// line looks lie:
						// ... //depot/path/to/file/file.ext#1 edit
						
						int revStart = entry.indexOf("#");
						String filename = entry.substring(0, revStart);
						String rev = entry.substring(revStart + 1, entry.indexOf(" ", revStart));
						String action = entry.substring(entry.indexOf(" ", revStart) + 1);
						Changelist.FileEntry file = new Changelist.FileEntry();
						file.setFilename(filename);
						file.setRevision(rev);
						file.setAction(Changelist.FileEntry.Action.valueOf(action.toUpperCase()));
						files.add(file);
					}
					
					change.setFiles(files);
	
				}
			}
		} catch(Exception e) {
			logger.error("Exception: " + e.getMessage());
			throw new PerforceException("Failed to retrieve changelist.", e);
		}
		return change;
	}
	
	

	public String[] getSaveCmd() {
		return new String[] { "p4", "change", "-i" };
	}

	/* (non-Javadoc)
	 * @see com.tek42.perforce.parse.Builder#save(java.lang.Object)
	 */
	public void save(Changelist obj, Writer out) throws PerforceException {
		throw new UnsupportedOperationException("This is not implemented yet."); 
	}
}
