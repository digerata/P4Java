package com.tek42.perforce;

import static org.junit.Assert.*;

import java.io.*;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.tek42.perforce.model.Workspace;

/**
 *
 * @author mwille
 *
 */
public class WorkspaceTest extends PropertySupport {
	Depot depot;
	static String ticket;
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
		depot.setP4Ticket(ticket);
	}

	/**
	 * Test method for {@link com.tek42.perforce.parse.Workspaces#getWorkspace(String)}.
	 */
	@Test
	public void testGetWorkspace() throws Exception {
		Workspace workspace = depot.getWorkspaces().getWorkspace(getProperty("ws.existingname"));
		System.out.println("Have workspace: \n" + workspace);
		// work around for saving to depot not authenticating first.  I KNOW. I KNOW.
		ticket = depot.getP4Ticket();
	}
	
	@Test
	public void testMyCreateWorkspace() throws Exception {
		Workspace workspace = new Workspace();
		String name = getProperty("ws.name");
		workspace.setName(name);
		workspace.setDescription(getProperty("ws.create.desc") + new Date());
		workspace.setHost(getProperty("ws.create.host"));
		workspace.setLineEnd(getProperty("ws.create.lineend"));
		workspace.setRoot(getProperty("ws.create.root"));
		workspace.setAltRoots(getProperty("ws.create.altroot"));
		workspace.setOptions(getProperty("ws.create.options"));
		workspace.setOwner(getProperty("ws.create.owner"));
		workspace.setSubmitOptions(getProperty("ws.create.submitoptions"));
		workspace.addView(getProperty("ws.create.view"));
		
		Workspace copy = workspace;
		
		depot.getWorkspaces().saveWorkspace(workspace);
		
		workspace = depot.getWorkspaces().getWorkspace(name);
		
		assertNotNull(workspace);
		
		// For some reason, this doesn't quite work...
		//assertEquals(copy, workspace);
		assertEquals(copy.getName(), workspace.getName());
		assertEquals(copy.getDescription(), workspace.getDescription());
		assertEquals(copy.getHost(), workspace.getHost());
		assertEquals(copy.getLineEnd(), workspace.getLineEnd());
		assertEquals(copy.getRoot(), workspace.getRoot());
		assertEquals(copy.getAltRoots(), workspace.getAltRoots());
		assertEquals(copy.getOptions(), workspace.getOptions());
		assertEquals(copy.getSubmitOptions(), workspace.getSubmitOptions());
		assertEquals(copy.getOwner(), workspace.getOwner());
		assertEquals(copy.getViewsAsString(), workspace.getViewsAsString());
	}

	@Test
	public void testUpdateWorkspace() throws Exception {
		Workspace workspace = depot.getWorkspaces().getWorkspace(getProperty("ws.name"));
		
		workspace.setRoot(getProperty("ws.update.root"));
		workspace.clearViews();
		workspace.addView(getProperty("ws.update.view"));
		String desc = getProperty("ws.update.desc") + new Date();
		workspace.setDescription(desc);
		workspace.setSubmitOptions(getProperty("ws.update.submitoptions"));
		
		depot.getWorkspaces().saveWorkspace(workspace);
		
		workspace = depot.getWorkspaces().getWorkspace(getProperty("ws.name"));
		assertNotNull(workspace);
		assertEquals(getProperty("ws.update.root"), workspace.getRoot());
		assertEquals(getProperty("ws.update.view") + "\n", workspace.getViewsAsString());
		assertEquals(desc, workspace.getDescription());
		
	}
	
	@Test
	public void testSyncToLabel() throws Exception {
		Workspace workspace = depot.getWorkspaces().getWorkspace(getProperty("p4.client"));
		File dir = new File(workspace.getRoot());
		if(!dir.exists()) {
			dir.mkdirs();
		}
		StringBuilder sb = depot.getWorkspaces().syncTo(getProperty("changelist.project") + "@TestLabel", true);
		System.out.println(sb);
		File test = new File(dir.getAbsolutePath() + "/" + getProjectFolder(getProperty("changelist.project")) + "/project-file.txt");
		System.out.println("Checking for file: " + test.getAbsolutePath());
		assertTrue("Workspace sync failed, file does not exist.", test.exists());
	}
	
	/**
	 * Test properties only has a depot path, we'll fix that here...
	 * @param depotPath
	 * @return
	 */
	private String getProjectFolder(String depotPath) {
		depotPath = depotPath.substring(7);
		depotPath = depotPath.replaceAll("/\\.\\.\\.", "");
		return depotPath;
		
	}
}
