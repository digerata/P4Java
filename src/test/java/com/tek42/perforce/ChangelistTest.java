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
public class ChangelistTest extends PropertySupport {
	Depot depot;
	
	/**
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		depot = new Depot();
		depot.setPort(getProperty("p4.port"));
		depot.setUser(getProperty("p4.user"));
		depot.setPassword(getProperty("p4.passwd"));
		depot.setClient(getProperty("p4.client"));
	}
	
	/**
	 * Test method for {@link com.tek42.perforce.Depot#getEnvp()}.
	 */
	@Test
	public void testGetChangelist() throws Exception {
		int id = new Integer(getProperty("changelist.middlechange"));
		Changelist change = depot.getChanges().getChangelist(id);
		System.out.println("\nRetrieved Changelist:\n" + change.toString());
	}

	/**
	 * Test method for {@link com.tek42.perforce.Depot#getChangelists(java.lang.String, int, int)}.
	 */
	@Test
	public void testGetLimitedChangelists() throws Exception {
		List<Changelist> changes = depot.getChanges().getChangelists(getProperty("changelist.project"), -1, 5);
		assertNotNull(changes);
		assertTrue(changes.size() == 5);
		
		String ids[] = getProperties("changelist.numbers");
		int i = 0;
		for(Changelist change: changes) {
			assertEquals(new Integer(ids[i++]).intValue(), change.getChangeNumber());
		}
	}
	
	@Test
	public void testGetAllChangelists() throws Exception {
		List<Changelist> changes = depot.getChanges().getChangelists(getProperty("changelist.project"), 0, -1);
		assertNotNull(changes);
		assertTrue(changes.size() > 0);
		
		String ids[] = getProperties("changelist.numbers");
		assertTrue(changes.size() == ids.length);
		int i = 0;
		for(Changelist change: changes) {
			assertEquals(new Integer(ids[i++]).intValue(), change.getChangeNumber());
		}		
	}

	@Test
	public void testGetLastestChange() throws Exception {
		int change = new Integer(getProperty("changelist.lastchange"));
		List<Changelist> changes = depot.getChanges().getChangelists(getProperty("changelist.project"), -1, 1);
		assertNotNull(changes);
		assertEquals(1, changes.size());
		assertEquals(change, changes.get(0).getChangeNumber());
	}
	
	@Test
	public void testGetChangeNumbers() throws Exception {
		List<Integer> numbers = depot.getChanges().getChangeNumbers(getProperty("changelist.project"), -1, 5);
		for(int num : numbers) {
			System.out.println("Found change: " + num);
		}
	}
	
	@Test
	public void testGetRange() throws Exception {
		int number = new Integer(getProperty("changelist.firstchange"));
		List<Integer> numbers = depot.getChanges().getChangeNumbersTo(getProperty("changelist.project"), number);
		String ids[] = getProperties("changelist.numbers");
		assertTrue(numbers.size() == ids.length);
		
		assertEquals(new Integer(getProperty("changelist.lastchange")), numbers.get(0));
		assertEquals(new Integer(getProperty("changelist.firstchange")), numbers.get(numbers.size() - 1));
	}
}
