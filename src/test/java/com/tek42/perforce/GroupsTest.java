package com.tek42.perforce;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

import com.tek42.perforce.model.Label;
import com.tek42.perforce.model.Group;

/**
 * @author Mike
 *         Date: Jul 21, 2008 3:47:46 PM
 */
public class GroupsTest extends PropertySupport {
	Depot depot;
	static String ticket;

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
		depot.setP4Ticket(ticket);
	}

	/**
	 * Test method for {@link com.tek42.perforce.parse.Labels#getLabels(java.lang.String)}.
	 */
	@Test
	public void testGetGroups() throws Exception {
		List<Group> groups = depot.getGroups().getGroups();
		for(Group g : groups) {
			System.out.println("Group: " + g.getName());
		}
	}

	/**
	 * Test method for {@link com.tek42.perforce.parse.Labels#getLabel(java.lang.String)}.
	 */
	@Test
	public void testGetGroup() throws Exception {
		Group group = depot.getGroups().getGroup(getProperty("group.name"));
		assertNotNull(group);
		assertEquals(getProperty("group.name"), group.getName());
		assertEquals(Long.parseLong(getProperty("group.timeout")), (long) group.getTimeout());
	}

	/**
	 * Test method for {@link com.tek42.perforce.parse.Labels#saveLabel(com.tek42.perforce.model.Label)}.
	 */
	@Test
	public void testCreateGroup() throws Exception {
		Group group = new Group();
		group.setName(getProperty("group.new.name"));
		List<String> users = new ArrayList<String>();
		users.add(getProperty("group.new.user").trim());
		group.setUsers(users);
		List<String> owners = new ArrayList<String>();
		owners.add(getProperty("p4.user").trim());
		group.setOwners(owners);
		group.setTimeout(Long.parseLong(getProperty("group.new.timeout")));

		Group copy = group;

		depot.getGroups().saveGroup(group);

		group = depot.getGroups().getGroup(getProperty("group.new.name"));

		assertNotNull(group);

		assertEquals(copy.getName(), group.getName());
		assertEquals(copy.getUsersAsString(), group.getUsersAsString());
		assertEquals(copy.getOwnersAsString(), group.getOwnersAsString());
		assertEquals(copy.getSubgroupsAsString(), group.getSubgroupsAsString());
		assertEquals(copy.getTimeout(), group.getTimeout());
		assertEquals(copy.getMaxLockTime(), group.getMaxLockTime());
		assertEquals(copy.getMaxResults(), group.getMaxResults());
		assertEquals(copy.getMaxScanRows(), group.getMaxScanRows());
	}

	@Test
	public void testSaveChanges() throws Exception {
		Group group = depot.getGroups().getGroup(getProperty("group.name"));

		group.setTimeout(100L);

		depot.getGroups().saveGroup(group);

		group = depot.getGroups().getGroup(getProperty("group.name"));

		assertNotNull(group);

		assertEquals(100L, (long) group.getTimeout());

		// Must reset data in perforce...
		group.setTimeout(Long.parseLong(getProperty("group.timeout")));
		depot.getGroups().saveGroup(group);
	}

	/**
	 * This guy is here because our main method to test login retries on expired session is to modify the timeout of
	 * the user's group.
	 *
	 * @throws Exception
	 */
	@Test
	public void testLoginExpiration() throws Exception {
		System.out.println("p4 info: \n" + depot.info());
		Group group = depot.getGroups().getGroup(getProperty("group.name"));
		// long enough that we can change this back when we are done without hitting the timeout...
		group.setTimeout(5L);
		depot.getGroups().saveGroup(group);
		group = depot.getGroups().getGroup(getProperty("group.name"));
		assertEquals(5L, (long) group.getTimeout());
		Thread.sleep(7000L);
		assertEquals(true, depot.getStatus().isValid());
		// We should get a PerforceException about not being logged in... (But we aren't under my test instance ??!!)
		group = depot.getGroups().getGroup(getProperty("group.name"));
		group.setTimeout(Long.parseLong(getProperty("group.timeout")));
		depot.getGroups().saveGroup(group);

	}
}
