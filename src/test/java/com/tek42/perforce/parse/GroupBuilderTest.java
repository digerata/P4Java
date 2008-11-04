package com.tek42.perforce.parse;

import java.io.InputStream;
import java.io.OutputStreamWriter;

import com.tek42.perforce.model.Group;
import junit.framework.TestCase;

/**
 * @author Mike
 *         Date: Jul 21, 2008 3:20:53 PM
 */
public class GroupBuilderTest  extends TestCase {

	public void testBuild() throws Exception {
		System.out.println("GroupBuilderTest.testBuild() >>>>>>>>>>>");
		InputStream is = this.getClass().getResourceAsStream("GroupTest.txt");

		StringBuilder sb = new StringBuilder();
		byte[] buff = new byte[10*1024*1024];
		int read = 0;
		while( (read = is.read(buff)) != -1 ) {
			String line = new String(buff, 0, read);
			sb.append(line);

		}

		GroupBuilder b = new GroupBuilder();
		Group g = b.build(sb);
		System.out.println("New Group:\n" + g);
	}

	public void testSave() throws Exception {
		System.out.println("GroupBuilderTest.testSave() >>>>>>>>>>>");
		InputStream is = this.getClass().getResourceAsStream("GroupTest.txt");

		StringBuilder sb = new StringBuilder();
		byte[] buff = new byte[10*1024*1024];
		int read = 0;
		while( (read = is.read(buff)) != -1 ) {
			String line = new String(buff, 0, read);
			sb.append(line);

		}

		GroupBuilder b = new GroupBuilder();
		Group g = b.build(sb);

		OutputStreamWriter osw = new OutputStreamWriter(System.out);
		b.save(g, osw);
		osw.flush();
	}
}
