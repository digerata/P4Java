package com.tek42.perforce;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
		depot.setPassword("");
		depot.setPort("codemaster.atdoner.com:1666");
	}

	/**
	 *
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.tek42.perforce.Depot#checkValidity()}.
	 */
	@Test
	public void testCheckValidity() throws Exception {
		System.out.println("Perforce Info: \n" + depot.info());
		depot.checkValidity();
	}

	/**
	 * Test method for {@link com.tek42.perforce.Depot#getEnvp()}.
	 */
	@Test
	public void testGetClient() {
		//depot.getW();
	}

}
