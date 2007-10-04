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
	
	/**
	 * A lightweight call to return changelist numbers for a given path.
	 * <p>
	 * To get the latest change in the depot for the project, you can use:
	 * <pre>depot.getChangeNumbers("//project/...", -1, 1)</pre>
	 * <p>
	 * Note: this method follows perforce in that it starts at the highest number and works 
	 * backwards.  So this might not be what you want.  (It certainly isn't for Hudson)
	 * 
	 * @param path			Path to filter on
	 * @param start			The number of the change to start from
	 * @param limit			The number of changes to return
	 * @return
	 * @throws PerforceException
	 */
	public List<Integer> getChangeNumbers(String path, int start, int limit) throws PerforceException {
		if(path == null || path.equals(""))
			path = "//...";
		if(start > 0)
			path += "@" + start; 
	
		String cmd[];
		
		if(limit > 0)
			cmd = new String[] {"p4", "changes", "-m", new Integer(limit).toString(), path };
		else
			cmd = new String[] {"p4", "changes", path };
		
		StringBuilder response = getPerforceResponse(cmd);
		List<String> ids = parseList(response, 1);
		List<Integer> numbers = new ArrayList<Integer>(ids.size());
		for(String id : ids) {
			numbers.add(new Integer(id));
		}
		return numbers;
	}
	
	/**
	 * Returns a list of changenumbers that start with the most recent change and work
	 * back to the specified change.
	 * 
	 * @param path
	 * @param untilChange
	 * @return
	 */
	public List<Integer> getChangeNumbersTo(String path, int untilChange) throws PerforceException {
		if(path == null || path.equals(""))
			path = "//...";
			
		String cmd[];
		
		cmd = new String[] {"p4", "changes", "-m", "25", path };
		
		List<Integer> ids = new ArrayList<Integer>();
		int counter = 0;
		String lastChange;
		OUTER: while(true) {
			System.out.println("Looping: " + counter++);
			StringBuilder response;
			try {
				// getPerforceResponse will throw an exception if a command it executes 
				// returns nothing from perforce.  If we are moving back through a list and have
				// less change lists in the history then what was specified, we will hit this 
				// exception
				response = getPerforceResponse(cmd);
			} catch(PerforceException e) {
				if(e.getMessage().startsWith("No output for"))
					break OUTER;
				throw e;
			}
			List<String> temp = parseList(response, 1);
			if(temp.size() == 0)
				break;
			for(String num : temp) {
				if(new Integer(num) >= untilChange)
					ids.add(new Integer(num));
				else
					break OUTER; 
			}
			lastChange = temp.get(temp.size() - 1);
			int next = new Integer(lastChange) - 1;
			cmd = new String[] {"p4", "changes", "-m", "25", path + "@" + next };
		}
		return ids;
	}
	
	/**
	 * Converts a list of numbers to a list of changes.
	 * 
	 * @param numbers
	 * @return
	 * @throws PerforceException
	 */
	public List<Changelist> getChangelistsFromNumbers(List<Integer> numbers) throws PerforceException {
		List<Changelist> changes = new ArrayList<Changelist>();
		for(Integer id : numbers) {
			changes.add(getChangelist(id));
		}
		return changes;
	}
}
