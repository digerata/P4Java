package com.tek42.perforce.parse;

import java.util.ArrayList;
import java.util.List;

import com.tek42.perforce.PerforceException;
import com.tek42.perforce.model.Changelist;
import com.tek42.perforce.Depot;

/**
 * Base API object for interacting with changelists
 * 
 * @author Mike Wille
 *
 */
public class Changes extends AbstractPerforceTemplate {
	
	public Changes(Depot depot) {
		super(depot);
	}
	
	/**
	 * Returns a single changelist specified by its number.
	 *
	 * @param number
	 * @return
	 * @throws PerforceException
	 */
	public Changelist getChangelist(int number) throws PerforceException {
		String id = new Integer(number).toString();
		ChangelistBuilder builder = new ChangelistBuilder();
		Changelist change = builder.build(getPerforceResponse(builder.getBuildCmd(id)));
		if(change == null)
			throw new PerforceException("Failed to retrieve changelist " + number);
		return change;
	}
	
	/**
	 * Returns a list of changelists that match the parameters
	 *
	 * @param path			What point in the depot to show changes for?
	 * @param lastChange	The last changelist number to start from
	 * @param limit			The maximum changes to return
	 * 						if less than 1, will return everything
	 * @return
	 * @throws PerforceException
	 */
	public List<Changelist> getChangelists(String path, int lastChange, int limit) throws PerforceException {
		if(path == null || path.equals(""))
			path = "//...";
		if(lastChange > 0)
			path += "@" + lastChange; 
	
		String cmd[];
		
		if(limit > 0)
			cmd = new String[] {"p4", "changes", "-m", new Integer(limit).toString(), path };
		else
			cmd = new String[] {"p4", "changes", path };
		
		StringBuilder response = getPerforceResponse(cmd);
		List<String> ids = parseList(response, 1);
		
		List<Changelist> changes = new ArrayList<Changelist>();
		for(String id : ids) {
			changes.add(getChangelist(new Integer(id)));
		}
		return changes;
	}
}
