package com.tek42.perforce.model;

import java.util.List;
import java.util.ArrayList;

/**
 * Provide base support for views.
 * 
 * @author Mike Wille
 *
 */
public abstract class AbstractViewsSupport {
	protected List<String> views;
	
	public AbstractViewsSupport() {
		views = new ArrayList<String>();
	}

	/**
	 * @return the view
	 */
	public List<String> getViews() {
		return views;
	}

	/**
	 * Returns the list of views concatenated together with \n
	 * as delimeters.
	 *
	 * @return
	 */
	public String getViewsAsString() {
		StringBuilder sb = new StringBuilder();
		for(String view : views) {
			sb.append(view + "\n");
		}
		return sb.toString();
	}
	
	/**
	 * @param view
	 *            the view to set
	 */
	public void addView(String view) {
		this.views.add(view);
	}

	/**
	 * Removes all views from this client.
	 */
	public void clearViews() {
		this.views.clear();
	}
}
