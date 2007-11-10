package com.tek42.perforce;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import com.tek42.perforce.model.*;

/**
 *
 * @author mwille
 *
 */
public class UserTest extends PropertySupport {
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

	@Test
	public void testGetUser() throws Exception {
		User user = depot.getUsers().getUser(getProperty("user.username"));
		assertNotNull(user);
		assertEquals(getProperty("user.username"), user.getUsername());
		assertEquals(getProperty("user.email"), user.getEmail());
		assertEquals(getProperty("user.fullname"), user.getFullName());
	}
	
	@Test
	public void testCreateUser() throws Exception {
		
	}
	
	@Test
	public void testSaveUser() throws Exception {
		
	}
}
