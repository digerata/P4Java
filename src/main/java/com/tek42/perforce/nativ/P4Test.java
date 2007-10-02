package com.tek42.perforce.nativ;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class P4Test {
	ProcessBuilder builder;
	List<String> args;
	
	public P4Test() {
		args = new ArrayList<String>();
		builder = new ProcessBuilder(args);
		Map<String, String> env = builder.environment();
		env.put("P4USER", "mwille");
		env.put("P4CLIENT", "mike-laptop");
		env.put("P4PORT", "codemaster.atdoner.com:1666");
		env.put("P4PASSWD", "");
		//env.put("PATH", "C:\\Program Files\\Perforce");
	}
	
	public ProcessBuilder getPB()
	{
		return builder;
	}
	
	public List<String> getArgs()
	{
		return args;
	}
	
	public static void main(String argz[]) throws Exception {
		P4Test test = new P4Test();
		test.getArgs().clear();
		test.getArgs().add("p4");
		test.getArgs().add("workspace");
		test.getArgs().add("-o");
		test.getArgs().add("test-workspace");
		
		test.getPB().redirectErrorStream(true);
		Process pr = test.getPB().start();
		BufferedReader output = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while((line = output.readLine()) != null) {
			if(line.startsWith("#"))
				continue;
			System.out.println(line);
			sb.append(line + "\n");
		}
		// Allow our regexp to match with only one case
		sb.append("Endp:\n");
		Pattern p = Pattern.compile("^(\\w+):(.*?)(?=\\n\\w{4,}?:)", Pattern.DOTALL | Pattern.MULTILINE);
		Matcher m = p.matcher(sb.toString());
		while(m.find()) {
			System.out.println("Key: " + m.group(1));
			System.out.println("Value: " + m.group(2).trim());
			System.out.println("=================================");
		}
		
	}
}
