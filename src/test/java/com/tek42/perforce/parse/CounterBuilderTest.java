package com.tek42.perforce.parse;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.tek42.perforce.PerforceException;
import com.tek42.perforce.model.Counter;

/**
 * @author Kamlesh Sangani
 */
public class CounterBuilderTest {

	@Test
	public void testBuild() throws PerforceException {
		final int counterValue = 456456;
		final StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(counterValue));
		sb.append("\r\n");
		sb.append("\r\n");
		final CounterBuilder counterBuilder = new CounterBuilder();
		final Counter counter = counterBuilder.build(sb);
		assertEquals("Counter names should be equal", "", counter.getName());
		assertEquals("Counter value should be equal", counterValue, counter.getValue());
	}

	@Test
	public void testGetSaveCmd() throws PerforceException {
		final String counterName = "test";
		final int counterValue = 100;
		final Counter counter = new Counter();
		counter.setName(counterName);
		counter.setValue(counterValue);

		final String[] commands = new CounterBuilder().getSaveCmd(counter);
		final String[] expectedCommands = new String[] { "p4", "counter", counterName, String.valueOf(counterValue) };

		assertEquals("Commands length should be equal", expectedCommands.length, commands.length);

		for(int i = 0; i < expectedCommands.length; i++) {
			assertEquals("Command should be equal", expectedCommands[i], commands[i]);
		}
	}

	@Test
	public void testGetBuildCmd() throws PerforceException {
		final String counterName = "test";
		final CounterBuilder counterBuilder = new CounterBuilder();
		final String[] commands = counterBuilder.getBuildCmd(counterName);
		final String[] expectedCommands = new String[] { "p4", "counter", counterName };

		assertEquals("Commands length should be equal", expectedCommands.length, commands.length);

		for(int i = 0; i < expectedCommands.length; i++) {
			assertEquals("Command should be equal", expectedCommands[i], commands[i]);
		}
	}

}
