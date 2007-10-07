package com.tek42.perforce;

import java.io.*;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/**
 *
 * @author mwille
 *
 */
public class DepotTest extends PropertySupport {
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
		
		String home = System.getProperty("user.home");
		System.out.println("Deleting p4 tickets under: " + home);
		new File(home + "/" + ".p4tickets").delete();
		new File(home + "\\" + "p4tickets.txt").delete();
	}

	/**
	 *
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testServerInfo() throws Exception {
		System.out.println("\nPerforce Info:\n" + depot.info());
	}
	
	/**
	 * Test method for {@link com.tek42.perforce.Depot#checkValidity()}.
	 */
	@Test
	public void testCheckValidity() throws Exception {
		assertTrue(depot.getStatus().isValid());
	}
}
