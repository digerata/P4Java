package com.tek42.perforce.parse;

import java.util.ArrayList;
import java.util.List;

import com.tek42.perforce.Depot;
import com.tek42.perforce.PerforceException;
import com.tek42.perforce.model.Label;

/**
 *
 * @author mwille
 *
 */
public class Labels extends AbstractPerforceTemplate {
	
	public Labels(Depot depot) {
		super(depot);
	}
	
	
	/**
	 * Handles both creating and saving labels.
	 *
	 * @param label
	 * @return
	 */
	public void saveLabel(Label label) throws PerforceException {
		LabelBuilder builder = new LabelBuilder();
		saveToPerforce(label, builder);
	}
	
	/**
	 * Returns a list of labels in the system.  Optionally, you can specify
	 * a path argument to return only labels that contain the specified path.   
	 *
	 * @param path
	 * @return
	 * @throws PerforceException
	 */
	public List<Label> getLabels(String path) throws PerforceException {
		String cmd[];
		
		if(path != null && !path.equals(""))
			cmd = new String[] { "p4", "labels", path };
		else
			cmd = new String[] { "p4", "labels" };
		
		List<Label> labels = new ArrayList<Label>();
		
		StringBuilder response = getPerforceResponse(cmd);
		List<String> names = parseList(response, 1);
		
		for(String name : names) {
			labels.add(getLabel(name));
		}
		
		return labels;
	}
		
	/**
	 * Returns a label specified by name.
	 *
	 * @param name
	 * @return
	 * @throws PerforceException
	 */
	public Label getLabel(String name) throws PerforceException {
		LabelBuilder builder = new LabelBuilder();
		Label label = builder.build(getPerforceResponse(builder.getBuildCmd(name)));
		return label;
	}
}
