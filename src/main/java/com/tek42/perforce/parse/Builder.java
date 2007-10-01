/**
 * 
 */
package com.tek42.perforce.parse;

import com.tek42.perforce.*;

/**
 * Interface for parsing perforce output into a concrete object and also
 * for saving the object back to perforce.
 * 
 * @author Michael Wille
 *
 */
public interface Builder<T> {
	public String[] getBuildCmd(String id);
	public T build(StringBuilder sb) throws PerforceException;
	public String[] getSaveCmd();
	public void save(T obj) throws PerforceException;
}
