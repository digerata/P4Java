package com.tek42.perforce.parse;

import java.io.*;
import java.util.StringTokenizer;

import com.tek42.perforce.model.*;
import com.tek42.perforce.PerforceException;

/**
 *
 * @author mwille
 *
 */
public class WorkspaceBuilder implements Builder<Workspace> {

	/* (non-Javadoc)
	 * @see com.tek42.perforce.parse.Builder#build(java.lang.StringBuilder)
	 */
	public Workspace build(StringBuilder sb) throws PerforceException {
		// Note: we particularly do NOT want tabs or spaces in our tokenizer.  Including tabs would
		// screw up how we read the description.
		StringTokenizer lines = new StringTokenizer(sb.toString(), "\n\r");
		Workspace workspace = new Workspace();
		while(lines.hasMoreTokens()) {
			String line = lines.nextToken();
			
			if(line.startsWith("#")) {
				continue;
			}
			
			if(line.startsWith("Client:")) {
				workspace.setName(line.substring(8).trim());

			} else if(line.startsWith("Owner:")) {
				workspace.setOwner(line.substring(7).trim());

			} else if(line.startsWith("Host:")) {
				workspace.setOwner(line.substring(6).trim());

			} else if(line.startsWith("Root:")) {
				workspace.setRoot(line.substring(6).trim());

			} else if(line.startsWith("Options:")) {
				workspace.setOptions(line.substring(9).trim());

			} else if(line.startsWith("SubmitOptions:")) {
				workspace.setSubmitOptions(line.substring(15).trim());

			} else if(line.startsWith("LineEnd:")) {
				workspace.setLineEnd(line.substring(9).trim());

			} else if(line.startsWith("AltRoots:")) {
				workspace.setAltRoots(line.substring(10).trim());

			} else if(line.startsWith("Description:")) {
				line = lines.nextToken();
				String desc = line.trim();
				while(line.startsWith("\\t") && lines.hasMoreElements() && !lines.equals("")) {
					desc += "\n" + line.trim(); // trim will get rid of the tab for us
					line = lines.nextToken();
				}
				workspace.setDescription(desc);
				
			} else if(line.startsWith("View:")) {
				line = lines.nextToken();
				while((line.startsWith("\t") || line.startsWith(" ") || line.startsWith("//")) && lines.hasMoreTokens()) {
					workspace.addView(line);
					line = lines.nextToken();
				}
			}
		}

		return workspace;
	}

	/* (non-Javadoc)
	 * @see com.tek42.perforce.parse.Builder#getBuildCmd(java.lang.String)
	 */
	public String[] getBuildCmd(String id) {
		return new String[] { "p4", "workspace", "-o", id };
	}

	/* (non-Javadoc)
	 * @see com.tek42.perforce.parse.Builder#getSaveCmd()
	 */
	public String[] getSaveCmd() {
		return new String[] { "p4", "workspace", "-i" };
	}

	/* (non-Javadoc)
	 * @see com.tek42.perforce.parse.Builder#save(java.lang.Object)
	 */
	public void save(Workspace workspace, Writer out) throws PerforceException {
		try {
			out.write("Client: " + workspace.getName() + "\n");
			if(!workspace.getOwner().equals(""))
				out.write("Owner: " + workspace.getOwner() + "\n");
			if(!workspace.getHost().equals(""))
				out.write("Host: " + workspace.getHost() + "\n");
			out.write("Description: " + workspace.getDescription() + "\n");
			out.write("Root: " + workspace.getRoot() + "\n");
			out.write("Options: " + workspace.getOptions() + "\n");
			out.write("LineEnd: " + workspace.getLineEnd() + "\n");
			out.write("View:\n");
			out.write(" " + workspace.getViewsAsString());
			if(!workspace.getAltRoots().equals(""))
				out.write("AltRoots: " + workspace.getAltRoots() + "\n");
			if(!workspace.getSubmitOptions().equals(""))
				out.write("SubmitOptions: " + workspace.getSubmitOptions() + "\n");
			out.flush();
		} catch(IOException e) {
			throw new PerforceException("Failed to save workspace", e);
		}
	}

}
