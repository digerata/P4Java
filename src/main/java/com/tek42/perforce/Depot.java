package com.tek42.perforce;

import java.io.*;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import com.tek42.perforce.nativ.P4Process;
import com.tek42.perforce.model.*;
import com.tek42.perforce.parse.*;

/**
 * Represents the main object from which to interact with a Perforce server 
 * 
 * @author Mike Wille
 *
 */
public class Depot {
	private static Depot depot;
	
	private HashMap<String, String> settings;
	private String pathSep;
	private String fileSep;
	private String[] envp;
	private boolean validEnvp;
	private String p4exe;
	private long threshold;
	
	/**
	 * If not using this in a Dependancy Injection environment, use this method to get ahold of the depot.
	 *
	 * @return
	 */
	public static Depot getInstance() {
		if(depot == null) {
			depot = new Depot();
		}
		
		return depot;
	}
	
	public Depot() {
		settings = new HashMap<String, String>();
		settings.put("P4USER", "robot");
		settings.put("P4CLIENT", "robot-client");
		settings.put("P4PORT", "localhost:1666");
		settings.put("P4PASSWD", "");
		settings.put("PATH", "C:\\Program Files\\Perforce");
		settings.put("CLASSPATH", "/usr/share/java/p4.jar");
		setSystemDrive("C:");
		setSystemRoot("C:\\WINDOWS");
		setExecutable("p4");
		setServerTimeout(10000);
		
		String os = System.getProperty("os.name");
		
		if(null == os) {
			return;
		}
		
		if(os.startsWith("Windows")) {
			settings.put("PATHEXT", ".COM;.EXE;.BAT;.CMD");
			String windir = System.getProperty("com.ms.windir");
			if(windir != null) {
				appendPath(windir.substring(0, 1) + "\\Program Files\\Perforce");
				setSystemDrive(windir.substring(0, 1));
				setSystemRoot(windir);
			}
		}
	}
	
	/**
	 * Returns a workspace specified by name.
	 *
	 * @param name
	 * @return
	 * @throws PerforceException
	 */
	public Workspace getWorkspace(String name) throws PerforceException {
		WorkspaceBuilder builder = new WorkspaceBuilder();
		Workspace workspace = builder.build(getPerforceResponse(builder.getBuildCmd(name)));
		if(workspace == null)
			throw new PerforceException("Failed to retrieve workspace: " + name);
		
		return workspace;
	}
	
	/**
	 * Saves changes to an existing workspace, or creates a new one.
	 *
	 * @param workspace
	 * @throws PerforceException
	 */
	public void saveWorkspace(Workspace workspace) throws PerforceException {
		WorkspaceBuilder builder = new WorkspaceBuilder();
		P4Process p = null;
		try {
			p = new P4Process(this);
			p.exec(builder.getSaveCmd());
			//builder.save(workspace, p.getWriter());
			FilterWriter writer = new FilterWriter(p.getWriter()) {
				public void write(String data) {
					System.out.print("Debug: " + data);
				}
			};
			builder.save(workspace, writer);
			p.close();
		} catch(IOException e) {
			throw new PerforceException("Failed to open connection to perforce", e);
		}
	}
	
	/**
	 * Returns a single changelist specified by its number.
	 *
	 * @param number
	 * @return
	 * @throws PerforceException
	 */
	public Changelist getChangelist(int number) throws PerforceException {
		String id = new Integer(number).toString();
		ChangelistBuilder builder = new ChangelistBuilder();
		Changelist change = builder.build(getPerforceResponse(builder.getBuildCmd(id)));
		if(change == null)
			throw new PerforceException("Failed to retrieve changelist " + number);
		return change;
	}
	
	/**
	 * Returns a list of changelists that match the parameters
	 *
	 * @param path			What point in the depot to show changes for?
	 * @param lastChange
	 * @param limit
	 * @return
	 * @throws PerforceException
	 */
	public List<Changelist> getChangelists(String path, int lastChange, int limit) throws PerforceException {
		if(limit < 1)
			limit = 100;
		if(path == null || path.equals(""))
			path = "//...";
		if(lastChange >= 0)
			path += "@" + lastChange; 
	
		String cmd[] = {"p4", "changes", "-m", new Integer(limit).toString(), path }; 
		
		StringBuilder response = getPerforceResponse(cmd);
		
		List<String> ids = parseList(response, 1);
		
		List<Changelist> changes = new ArrayList<Changelist>();
		for(String id : ids) {
			changes.add(getChangelist(new Integer(id)));
		}
		return changes;
	}
	
	/**
	 * Parses lines of formatted text for a list of values.  Tokenizes each line into columns
	 * and adds the column specified by index to the list.
	 *
	 * @param response
	 * @param index
	 * @return
	 */
	protected List<String> parseList(StringBuilder response, int index) {
		StringTokenizer lines = new StringTokenizer(response.toString(), "\n\r");
		List<String> list = new ArrayList<String>(100);
		while(lines.hasMoreElements()) {
			StringTokenizer columns = new StringTokenizer(lines.nextToken());
			for(int column = 0; column < index; column++) {
				columns.nextToken();
			}
			list.add(columns.nextToken());
		}
		return list;
		
	}
	
	/**
	 * Executes a perforce command and returns the output as a StringBuilder.
	 *
	 * @param cmd
	 * @return
	 * @throws PerforceException
	 */
	protected StringBuilder getPerforceResponse(String cmd[]) throws PerforceException {
		P4Process p = null;
		try {
			StringBuilder sb = new StringBuilder();
			p = new P4Process(this);
			p.exec(cmd);
			String line;
			while((line = p.readLine()) != null) {
				sb.append(line + "\n");
			}
			return sb;
		} catch(IOException ex) {
			if(null != p) {
				try {
					p.close();
				} catch(Exception ignex) { /* Ignored Exception */
				}
			}
			throw new PerforceException(ex.getMessage());			
		}
	}
	
	/**
	 * Checks the environment to see if it is valid. To check the validity of
	 * the environment, the user information is accessed. This ensures that the
	 * server can be contacted and that the password is set properly.
	 * <p>
	 * If the environment is valid, this method will return quietly. Otherwise,
	 * it will throw a <code>PerforceException</code> with a message regarding
	 * the failure.
	 */
	public void checkValidity() throws PerforceException {
		String[] msg = { "Connect to server failed; check $P4PORT", 
				"Perforce password (P4PASSWD) invalid or unset.",
				"Can't create a new user - over license quota." };
		int msgndx = -1, i, cnt = 0;

		P4Process p = null;
		String l;
		String cmd[] = { "p4", "user", "-o" };

		try {
			p = new P4Process(this);
			p.exec(cmd);
			while(null != (l = p.readLine())) {
				cnt++;
				for(i = 0; i < msg.length; i++) {
					if(-1 != l.indexOf(msg[i]))
						msgndx = i;
				}
			}
			p.close();
		} catch(IOException ex) {
			if(null != p) {
				try {
					p.close();
				} catch(Exception ignex) { /* Ignored Exception */
				}
			}
		}
		if(-1 != msgndx)
			throw new PerforceException(msg[msgndx]);
		if(0 == cnt)
			throw new PerforceException("No output from p4 user -o");
	}
	
	/**
	 * Returns the output created by "p4 info"
	 *
	 * @return
	 */
	public String info() throws Exception {
		P4Process p = new P4Process(this);
		String cmd[] = {"p4", "info" };
		p.exec(cmd);
		StringBuilder sb = new StringBuilder();
		String line;
		while((line = p.readLine()) != null) {
			sb.append(line + "\n");
		}
		return sb.toString();
	}
	
	/**
	 * Returns the environment in a <code>String</code> array.
	 */
	public String[] getEnvp() {
		if(!validEnvp) {
			synchronized(settings) {
				envp = new String[settings.size()];
				int i = 0;
				for(String key : settings.keySet()) {
					envp[i++] = key + "=" + settings.get(key);
					//System.out.println("envp[" + i + "] = " + key + "=" + settings.get(key));
				}
			}
			validEnvp = true;
		}
		return envp;
	}
	
	/**
	 * Gets a property specified by key
	 *
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		return settings.get(key);
	}
	
	/**
	 * Gets a value specified by key.  If the value is empty, it will return
	 * the specified default.
	 *
	 * @param key
	 * @param def
	 * @return
	 */
	public String getProperty(String key, String def) {
		String value = getProperty(key);
		if(value == null || value.equals(""))
			return def;
		return value;
	}
	
	/**
	 * Sets the P4USER in the class information.
	 * 
	 * @param user
	 *            P4USER value.
	 */
	public void setUser(String user) {
		if(null == user)
			return;
		settings.put("P4USER", user);
		validEnvp = false;
	}

	/** Returns the P4USER. */
	public String getUser() {
		return (String) settings.get("P4USER");
	}

	/**
	 * Sets the P4CLIENT in the class information.
	 * 
	 * @param user
	 *            P4CLIENT value.
	 */
	public void setClient(String client) {
		if(null == client)
			return;
		settings.put("P4CLIENT", client);
		validEnvp = false;
	}

	/** Returns the P4CLIENT. */
	public String getClient() {
		return (String) settings.get("P4CLIENT");
	}

	/**
	 * Sets the P4PORT in the class information.
	 * 
	 * @param user
	 *            P4PORT value.
	 */
	public void setPort(String port) {
		if(null == port)
			return;
		settings.put("P4PORT", port);
		validEnvp = false;
	}

	/** Returns the P4PORT. */
	public String getPort() {
		return (String) settings.get("P4PORT");
	}

	/**
	 * Sets the P4PASSWD in the class information.
	 * 
	 * @param user
	 *            P4PASSWD value.
	 */
	public void setPassword(String password) {
		if(null == password)
			return;
		settings.put("P4PASSWD", password);
		validEnvp = false;
	}

	/** Returns the P4PASSWORD. */
	public String getPassword() {
		return (String) settings.get("P4PASSWD");
	}

	/**
	 * Sets the PATH in the class information.
	 * 
	 * @param path
	 *            PATH value.
	 */
	public void setPath(String path) {
		if(null == path)
			return;
		settings.put("PATH", path);
		validEnvp = false;
	}

	/**
	 * Append the path element to the existing path. If the path element given
	 * is already in the path, no change is made.
	 * 
	 * @param path
	 *            the path element to be appended.
	 */
	public void appendPath(String path) {
		String tok;
		if(null == path)
			return;
		String origPath = getProperty("PATH");
		if(null == pathSep || null == origPath) {
			setPath(path);
			return;
		}
		StringTokenizer st = new StringTokenizer(origPath, pathSep);
		StringBuffer sb = new StringBuffer();
		while(st.hasMoreTokens()) {
			tok = (String) st.nextToken();
			if(tok.equals(path))
				return;
			sb.append(tok);
			sb.append(pathSep);
		}
		sb.append(path);
		setPath(path);
	}

	/**
	 * Returns the path
	 *
	 * @return
	 */
	public String getPath() {
		return (String) settings.get("PATH");
	}

	/**
	 * Sets the SystemDrive in the class information. This is only meaningful
	 * under Windows.
	 * 
	 * @param user
	 *            SystemDrive value.
	 */
	public void setSystemDrive(String drive) {
		if(null == drive)
			return;
		settings.put("SystemDrive", drive);
		validEnvp = false;
	}

	/**
	 * Sets the SystemRoot in the class information. This is only meaningful
	 * under Windows.
	 * 
	 * @param user
	 *            SystemRoot value.
	 */
	public void setSystemRoot(String root) {
		if(null == root)
			return;
		settings.put("SystemRoot", root);
		validEnvp = false;
	}

	/**
	 * Sets up the path to reach the p4 executable. The full path passed in must
	 * contain the executable or at least end in the system's file separator
	 * character. This gotten from the file.separator property. For example:
	 * 
	 * <pre>
	 * p4.executable=/usr/bin/p4   # This will work
	 * p4.executable=/usr/bin/     # This will work
	 * &lt;font color=Red&gt;p4.executable=/usr/bin      # This won't work&lt;/font&gt;
	 * </pre>
	 * 
	 * @param exe
	 *            Full path to the p4 executable.
	 */
	public void setExecutable(String exe) {
		int pos;
		if(null == exe)
			return;
		p4exe = exe;
		if(null == fileSep) {
			fileSep = System.getProperties().getProperty("file.separator", "\\");
		}
		if(-1 == (pos = exe.lastIndexOf(fileSep)))
			return;
		if(null == pathSep) {
			pathSep = System.getProperties().getProperty("path.separator", ";");
		}
		appendPath(exe.substring(0, pos));
		validEnvp = false;
	}

	/** Returns the path to the executable. */
	public String getExecutable() {
		return p4exe;
	}

	/** Set the server timeout threshold. */
	public void setServerTimeout(long threshold) {
		this.threshold = threshold;
	}

	/** Return the server timeout threshold. */
	public long getServerTimeout() {
		return threshold;
	}

}
