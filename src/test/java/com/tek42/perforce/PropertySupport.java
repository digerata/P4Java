package com.tek42.perforce;

import java.io.*;
import java.util.*;

/**
 * Provides support for loading test configuration from a properties file.
 * 
 * @author Mike Wille
 * 
 */
public abstract class PropertySupport {
	protected Properties props;
	protected String PROP_FILE = "test";

	/**
	 * Looks up a property in the PROP_FILE
	 *
	 * @param key
	 * @return The value if it exists or blank if the key did not exist.
	 */
	public String getProperty(String key) {
		if(props == null) {
			try {
				// InputStream stream =
				// getClass().getClassLoader().getResourceAsStream (PROP_FILE);
				String packageName = getClass().getPackage().getName().replaceAll("\\.", "/");
				props = loadProperties(packageName + "/" + PROP_FILE);
			} catch(Exception e) {
				// file doesn't exist, fail silently...
				System.err.println("Failed to load test properties: " + PROP_FILE);
				System.err.println(e.getMessage());
				e.printStackTrace();
				return "";
			}
		}
		String value = props.getProperty(key);
		if(value == null)
			return "";
		return value.trim();
	}
	
	/**
	 * Returns a 1D string array of values for a property that had a delimited list of values.
	 *
	 * @param key
	 * @return
	 */
	public String[] getProperties(String key) {
		return getProperty(key).split(",");
	}

	/**
	 * Looks up a resource named 'name' in the classpath. The resource must map
	 * to a file with .properties extention. The name is assumed to be absolute
	 * and can use either "/" or "." for package segment separation with an
	 * optional leading "/" and optional ".properties" suffix. Thus, the
	 * following names refer to the same resource:
	 * 
	 * <pre>
	 * some.pkg.Resource
	 * some.pkg.Resource.properties
	 * some/pkg/Resource
	 * some/pkg/Resource.properties
	 * /some/pkg/Resource
	 * /some/pkg/Resource.properties
	 * </pre>
	 * 
	 * @param name
	 *            classpath resource name [may not be null]
	 * @param loader
	 *            classloader through which to load the resource [null is
	 *            equivalent to the application loader]
	 * 
	 * @return resource converted to java.util.Properties [may be null if the
	 *         resource was not found and THROW_ON_LOAD_FAILURE is false]
	 * @throws IllegalArgumentException
	 *             if the resource was not found and THROW_ON_LOAD_FAILURE is
	 *             true
	 */
	public static Properties loadProperties(String name, ClassLoader loader) {
		if(name == null)
			throw new IllegalArgumentException("null input: name");

		if(name.startsWith("/"))
			name = name.substring(1);

		if(name.endsWith(SUFFIX))
			name = name.substring(0, name.length() - SUFFIX.length());

		Properties result = null;

		InputStream in = null;
		try {
			if(loader == null)
				loader = ClassLoader.getSystemClassLoader();

			if(LOAD_AS_RESOURCE_BUNDLE) {
				name = name.replace('/', '.');
				// Throws MissingResourceException on lookup failures:
				final ResourceBundle rb = ResourceBundle.getBundle(name, Locale.getDefault(), loader);

				result = new Properties();
				for(Enumeration<String> keys = rb.getKeys(); keys.hasMoreElements();) {
					final String key = keys.nextElement();
					final String value = rb.getString(key);

					result.put(key, value);
				}
			} else {
				name = name.replace('.', '/');

				if(!name.endsWith(SUFFIX))
					name = name.concat(SUFFIX);

				// Returns null on lookup failures:
				in = loader.getResourceAsStream(name);
				if(in != null) {
					result = new Properties();
					result.load(in); // Can throw IOException
				}
			}
		} catch(Exception e) {
			result = null;
		} finally {
			if(in != null)
				try {
					in.close();
				} catch(Throwable ignore) {
				}
		}

		if(THROW_ON_LOAD_FAILURE && (result == null)) {
			throw new IllegalArgumentException("could not load [" + name + "]" + " as "
					+ (LOAD_AS_RESOURCE_BUNDLE ? "a resource bundle" : "a classloader resource"));
		}

		return result;
	}

	/**
	 * A convenience overload of {@link #loadProperties(String, ClassLoader)}
	 * that uses the current thread's context classloader.
	 */
	public static Properties loadProperties(final String name) {
		return loadProperties(name, Thread.currentThread().getContextClassLoader());
	}

	private static final boolean THROW_ON_LOAD_FAILURE = true;

	private static final boolean LOAD_AS_RESOURCE_BUNDLE = false;

	private static final String SUFFIX = ".properties";
}
