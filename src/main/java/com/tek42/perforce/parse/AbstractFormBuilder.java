package com.tek42.perforce.parse;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tek42.perforce.PerforceException;

/**
 * Abstract class that parses the stringbuilder into key/value pairs and then
 * sends them to a abstract method responsible for building the object.  If you extend this
 * class, you do NOT override build(StringBuilder) but buildForm(Map).
 * <p>
 * Useful for all perforce objects that are editable via forms.  i.e., User, Workspace, Jobspec, etc.
 *  
 * @author Mike Wille
 *
 */
public abstract class AbstractFormBuilder<T> implements Builder<T> {
	private final Logger logger = LoggerFactory.getLogger("perforce");
	
	/* (non-Javadoc)
	 * @see com.tek42.perforce.parse.Builder#build(java.lang.StringBuilder)
	 */
	public T build(StringBuilder sb) throws PerforceException {
		// Allow our regexp to matcah with only one case and not have to handle the case for the last line
		sb.append("Endp:\n");
		logger.debug("Parsing: \n" + sb);
		Pattern p = Pattern.compile("^(\\w+):(.*?)(?=\\n\\w{4,}?:)", Pattern.DOTALL | Pattern.MULTILINE);
		Matcher m = p.matcher(sb.toString());
		Map<String, String> fields = new HashMap<String, String>();
		logger.debug("Parsing response...");
		while(m.find()) {
			String key = m.group(1);
			String value = m.group(2).trim();
			fields.put(key, value);
			logger.debug("Have key: " + key + " = " + value);
		}
		return buildForm(fields);
	}

	/**
	 * Test for null and returns an empty string if the key is not present.  Otherwise,
	 * returns the value.
	 *
	 * @param key
	 * @param fields
	 * @return
	 */
	protected String getField(String key, Map<String, String> fields) {
		String value = fields.get(key);
		if(value == null)
			return "";
		
		return value;
	}
	
	/**
	 * Should return a new object set with the data from fields.
	 *
	 * @param fields
	 * @return
	 * @throws PerforceException
	 */
	public abstract T buildForm(Map<String, String> fields) throws PerforceException;
}
