package com.tek42.perforce.parse;

import java.io.*;

import com.tek42.perforce.PerforceException;
import com.tek42.perforce.model.Changelist;
import com.tek42.perforce.model.Workspace;

import junit.framework.TestCase;

public class WorkspaceBuilderTest extends TestCase {

	public void testBuild() throws Exception {
		InputStream is = this.getClass().getResourceAsStream("Clientspec-Serverv2003-Issue-1070.txt");

		StringBuilder sb = new StringBuilder();
		byte[] buff = new byte[10*1024*1024];
		int read = 0;
		while( (read = is.read(buff)) != -1 ) {
			String line = new String(buff, 0, read);
			sb.append(line);

		}

		WorkspaceBuilder wb = new WorkspaceBuilder();
		Workspace w = wb.build(sb);
		System.out.println("Changelist: " + w);
	}

}
