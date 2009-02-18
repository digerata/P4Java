package com.tek42.perforce;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.tek42.perforce.model.Changelist;

/**
 * @author mwille
 */
public class ChangelistTest extends PropertySupport {
	Depot depot;

	/**
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
		assertEquals(4, changes.size());

		String ids[] = getProperties("changelist.numbers");
		int i = 0;
		for(Changelist change : changes) {
			assertEquals(new Integer(ids[i++]).intValue(), change.getChangeNumber());
		}
	}

	@Test
	public void testGetAllChangelists() throws Exception {
		List<Changelist> changes = depot.getChanges().getChangelists(getProperty("changelist.project"), 0, -1);
		assertNotNull(changes);
		assertTrue(changes.size() > 0);

		String ids[] = getProperties("changelist.numbers");
		assertEquals(ids.length, changes.size());
		int i = 0;
		for(Changelist change : changes) {
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
		assertEquals(ids.length, numbers.size());

		assertEquals(new Integer(getProperty("changelist.lastchange")), numbers.get(0));
		assertEquals(new Integer(getProperty("changelist.firstchange")), numbers.get(numbers.size() - 1));
	}

	@Test
	public void testGetChangeNumbersNotInclusive() throws Exception {
		int change = new Integer(getProperty("changelist.lastchange"));
		System.out.println("Last change listed in config: " + change);
		List<Changelist> changes = depot.getChanges().getChangelistsFromNumbers(
				depot.getChanges().getChangeNumbersTo(getProperty("changelist.project"), change + 1));
		assertEquals(0, changes.size());

		changes = depot.getChanges().getChangelists(getProperty("changelist.project"), -1, 1);
		assertEquals(change, changes.get(0).getChangeNumber());
		System.out.println("Latest change in depot is: " + changes.get(0).getChangeNumber());
	}

	// @Test
	public void testCreateChangeList() throws Exception {
		Changelist changelist = new Changelist();
		changelist.setDescription("desc");

		Depot.getInstance().getChanges().createChangelist(changelist);
		assertNotNull(changelist.getChangeNumber());
		assertTrue(changelist.isPending());
		assertNotNull(depot.getChanges().getChangelist(changelist.getChangeNumber()));
	}

}
