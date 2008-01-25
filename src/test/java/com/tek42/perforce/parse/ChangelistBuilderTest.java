package com.tek42.perforce.parse;

import java.io.*;

import com.tek42.perforce.PerforceException;
import com.tek42.perforce.model.Changelist;

import junit.framework.TestCase;

public class ChangelistBuilderTest extends TestCase {

	public void testBuild() throws Exception {
		//InputStream is = new FileInputStream("ChangelistTest-Issue-1092.txt");
		InputStream is = this.getClass().getResourceAsStream("ChangelistTest-Issue-1092.txt");

		StringBuilder sb = new StringBuilder();
		byte[] buff = new byte[10*1024*1024];
		int read = 0;
		while( (read = is.read(buff)) != -1 ) {
			String line = new String(buff, 0, read);
			sb.append(line);

		}

		ChangelistBuilder b = new ChangelistBuilder();
		Changelist c = b.build(sb);
		System.out.println("Changelist: " + c);
	}

}
