package com.tek42.perforce;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.tek42.perforce.model.*;

/**
 *
 * @author mwille
 *
 */
public class LabelsTest extends PropertySupport {
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
	 * Test method for {@link com.tek42.perforce.parse.Labels#saveLabel(com.tek42.perforce.model.Label)}.
	 */
	@Test
	public void testSaveLabel() throws Exception {
		Label label = new Label();
		label.setName(getProperty("label.name"));
		label.setDescription(getProperty("label.desc"));
		label.setOptions(getProperty("label.options"));
		label.setOwner(getProperty("label.owner"));
		label.setRevision(getProperty("label.revision"));
		label.addView(getProperty("label.view"));
		
		Label copy = label;
		
		depot.getLabels().saveLabel(label);
		
		label = depot.getLabels().getLabel(getProperty("label.name"));
		
		assertNotNull(label);
		
		assertEquals(copy.getName(), label.getName());
		assertEquals(copy.getDescription(), label.getDescription());
		assertEquals(copy.getOptions(), label.getOptions());
		assertEquals(copy.getOwner(), label.getOwner());
		assertEquals(copy.getRevision(), label.getRevision());
		assertEquals(copy.getViewsAsString(), label.getViewsAsString());
	}

	/**
	 * Test method for {@link com.tek42.perforce.parse.Labels#getLabels(java.lang.String)}.
	 */
	@Test
	public void testGetLabels() throws Exception {
				
	}

	/**
	 * Test method for {@link com.tek42.perforce.parse.Labels#getLabel(java.lang.String)}.
	 */
	@Test
	public void testGetLabel() throws Exception {
		Label label = depot.getLabels().getLabel(getProperty("label.name"));
		assertNotNull(label);
		assertEquals(getProperty("label.name"), label.getName());
	}

}
