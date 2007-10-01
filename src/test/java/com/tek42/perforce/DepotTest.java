package com.tek42.perforce;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tek42.perforce.model.*;
/**
 *
 * @author mwille
 *
 */
public class DepotTest {
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
		depot.checkValidity();
	}

	/**
	 * Test method for {@link com.tek42.perforce.Depot#getEnvp()}.
	 */
	@Test
	public void testGetChangelist() throws Exception {
		Changelist change = depot.getChangelist(8844);
		System.out.println("\nRetrieved Changelist:\n" + change.toString());
	}

}
