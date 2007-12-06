package com.tek42.perforce.parse;

import com.tek42.perforce.Depot;
import com.tek42.perforce.PerforceException;
import com.tek42.perforce.model.Workspace;

/**
 * Base API object for interacting with workspaces.
 * 
 * @author Mike Wille
 */
public class Workspaces extends AbstractPerforceTemplate {
	public Workspaces(Depot depot) {
		super(depot);
	}

	/**
	 * Returns a workspace specified by name.
	 * 
	 * @param name
	 * @return
	 * @throws PerforceException
	 */
	public Workspace getWorkspace(String name) throws PerforceException {
		WorkspaceBuilder builder = new WorkspaceBuilder();
		Workspace workspace = builder.build(getPerforceResponse(builder.getBuildCmd(name)));
		if(workspace == null)
			throw new PerforceException("Failed to retrieve workspace: " + name);

		return workspace;
	}

	/**
	 * Saves changes to an existing workspace, or creates a new one.
	 * 
	 * @param workspace
	 * @throws PerforceException
	 */
	public void saveWorkspace(Workspace workspace) throws PerforceException {
		WorkspaceBuilder builder = new WorkspaceBuilder();
		saveToPerforce(workspace, builder);
	}

	/**
	 * Synchronizes to the latest change for the specified path.  Convenience function
	 * for {@see syncTo(String, boolean)}
	 * 
	 * @param path
	 * @return
	 * @throws PerforceException
	 */
	public StringBuilder syncToHead(String path) throws PerforceException {
		return syncToHead(path, false);
	}

	/**
	 * Synchronizes to the latest change for the specified path. Allows a force sync to be performed by passing true to
	 * forceSync parameter.
	 * 
	 * @param path
	 *            The depot path to sync to
	 * @param forceSync
	 *            True to force sync and overwrite local files
	 * @return StringBuilder containing output of p4 response.
	 * @throws PerforceException
	 */
	public StringBuilder syncToHead(String path, boolean forceSync) throws PerforceException {
		if(!path.endsWith("#head")) {
			path += "#head";
		}
		return syncTo(path, forceSync);
	}
	
	/**
	 * Provides method to sync to a depot path and allows for any revision, changelist, label, etc.
	 * to be appended to the path.
	 * <p>
	 * A force sync can be specified by passing true to forceSync.
	 * 
	 * @param path
	 * 				The depot path to sync to.  Perforce suffix for [revRange] is allowed.
	 * @param forceSync
	 * 				Should we force a sync to grab all files regardless of version on disk?
	 * @return
	 * 			A StringBuilder that contains the output of the p4 execution.
	 * @throws PerforceException
	 */
	public StringBuilder syncTo(String path, boolean forceSync) throws PerforceException {
		if(forceSync)
			return getPerforceResponse(new String[] { "p4", "sync", "-f", path });
		else
			return getPerforceResponse(new String[] { "p4", "sync", path });
	}
}
