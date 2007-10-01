package com.tek42.perforce.parse;

import com.tek42.perforce.PerforceException;
import com.tek42.perforce.model.Changelist;
/**
 * Builder for changelists
 * 
 * @author Michael Wille
 *
 */
public class ChangelistBuilder implements Builder<Changelist> {

	public String[] getBuildCmd(String id) {
		return new String[] { "p4", "describe", "-s", id };
	}
	
	/* (non-Javadoc)
	 * @see com.tek42.perforce.parse.Builder#build(java.lang.StringBuilder)
	 */
	public Changelist build(StringBuilder sb) throws PerforceException {
		Changelist change = new Changelist();
			
		return change;
	}
	
	

	public String[] getSaveCmd() {
		return new String[] { "p4", "change", "-i" };
	}

	/* (non-Javadoc)
	 * @see com.tek42.perforce.parse.Builder#save(java.lang.Object)
	 */
	public void save(Changelist obj) throws PerforceException {
		// TODO Auto-generated method stub

	}

}
