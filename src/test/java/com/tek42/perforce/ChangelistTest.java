package com.tek42.perforce;

import java.util.*;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.tek42.perforce.model.Changelist;

/**
 *
 * @author mwille
 *
 */
public class ChangelistTest {
	Depot depot;
	
	/**
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		depot = new Depot();
		depot.setUser("mwille");
		depot.setPassword("phatpimp");
		depot.setPort("codemaster.atdoner.com:1666");
	}
	
	/**
	 * Test method for {@link com.tek42.perforce.Depot#getEnvp()}.
	 */
	@Test
	public void testGetChangelist() throws Exception {
		Changelist change = depot.getChangelist(8844);
		System.out.println("\nRetrieved Changelist:\n" + change.toString());
	}

	/**
	 * Test method for {@link com.tek42.perforce.Depot#getChangelists(java.lang.String, int, int)}.
	 */
	@Test
	public void testGetChangelists() throws Exception {
		List<Changelist> changes = depot.getChangelists("//depot/Extranet...", -1, 5);
		for(Changelist change: changes) {
			System.out.println(change);
		}
	}

}
