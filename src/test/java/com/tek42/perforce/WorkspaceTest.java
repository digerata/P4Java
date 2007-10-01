package com.tek42.perforce;

import java.util.*;
import com.tek42.perforce.*;
import com.tek42.perforce.model.*;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author mwille
 *
 */
public class WorkspaceTest {
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
	 * Test method for {@link com.tek42.perforce.Depot#getWorkspace(java.lang.String)}.
	 */
	@Test
	public void testGetWorkspace() throws Exception {
		Workspace workspace = depot.getWorkspace("mike-work");
		System.out.println("Have workspace: \n" + workspace);
	}
	
	@Test
	public void testUpdateWorkspace() throws Exception {
		Workspace workspace = depot.getWorkspace("test-workspace");
		System.out.println("Have test workspace:\n\n" + workspace);
		workspace.setRoot("c:/my/test/folder");
		workspace.clearViews();
		workspace.addView("//depot/Test/... //test-workspace/...");
		String desc = "I modified this on " + new Date();
		workspace.setDescription(desc);
		workspace.setSubmitOptions("revertunchanged");
		
		System.out.println("\n\nMade Changes:\n\n" + workspace);
		
		depot.saveWorkspace(workspace);
		
		workspace = depot.getWorkspace("test-workspace");
		assertNotNull(workspace);
		assertEquals("c:/my/test/folder", workspace.getRoot());
		assertEquals("//depot/Test/... //test-workspace/...", workspace.getViewsAsString());
		assertEquals(desc, workspace.getDescription());
	}
	
	//@Test
	public void testCreateWorkspace() throws Exception {
		Workspace workspace = new Workspace();
		String name = "test-workspace";
		workspace.setName(name);
		workspace.setDescription("Testing workspace created on: " + new Date());
		workspace.setHost("anyhost");
		workspace.setLineEnd("unix");
		workspace.setAltRoots("/Users/mwille/dev");
		workspace.setOptions("noallwrite noclobber compress nolocked nomodtime normdir");
		workspace.setOwner("mwille");
		workspace.setSubmitOptions("revertunchanged");
		
		Workspace copy = workspace;
		
		depot.saveWorkspace(workspace);
		
		workspace = depot.getWorkspace(name);
		
		assertNotNull(workspace);
		
		assertEquals(copy, workspace);
		
	}

}
