package com.tek42.perforce;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.tek42.perforce.model.Counter;

/**
 * @author Kamlesh Sangani
 */
public class CounterTest extends PropertySupport {

	private Depot depot;

	/**
	 * @throws com.tek42.perforce.PerforceException
	 */
	@Before
	public void setUp() throws PerforceException {
		this.depot = new Depot();
		this.depot.setPort(getProperty("p4.port"));
		this.depot.setUser(getProperty("p4.user"));
		this.depot.setPassword(getProperty("p4.passwd"));
		this.depot.setClient(getProperty("p4.client"));
	}

	@Test
	public void testGetCounter() throws PerforceException {
		final Counter counter = this.depot.getCounters().getCounter("change");
		assertEquals("Counter names should be equal", "change", counter.getName());
		assertTrue("Counter value should be >= 0", counter.getValue() >= 0);
	}

	@Test
	public void testGetCounters() throws PerforceException {
		final List<Counter> counters = this.depot.getCounters().getCounters();
		for(final Counter counter : counters) {
			assertNotNull("Counter name should not be null", counter.getName());
			assertTrue("Counter value should be >= 0", counter.getValue() >= 0);
		}
	}

	@Test
	public void testSaveCounter() throws PerforceException {
		final Counter counter = new Counter();
		counter.setName("test");
		counter.setValue(101);

		this.depot.getCounters().saveCounter(counter);

		final Counter actualCounter = this.depot.getCounters().getCounter(counter.getName());
		assertEquals("Counter names should be equal", counter.getName(), actualCounter.getName());
		assertEquals("Counter value should be equal", counter.getValue(), actualCounter.getValue());
	}
}
