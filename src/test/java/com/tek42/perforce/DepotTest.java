package com.tek42.perforce;

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
		assertTrue(depot.isValid());
	}
}
