package com.tek42.perforce.parse;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import com.tek42.perforce.PerforceException;
import com.tek42.perforce.model.Label;

/**
 *
 * @author mwille
 *
 */
public class LabelBuilder extends AbstractFormBuilder<Label> {

	/* (non-Javadoc)
	 * @see com.tek42.perforce.parse.AbstractFormBuilder#buildForm(java.util.Map)
	 */
	@Override
	public Label buildForm(Map<String, String> fields) throws PerforceException {
		Label label = new Label();
		label.setName(fields.get("Label"));
		label.setAccess(getField("Access", fields));
		label.setUpdate(getField("Update", fields));
		label.setDescription(getField("Description", fields));
		label.setOptions(getField("Options", fields));
		label.setOwner(getField("Owner", fields));
		label.setRevision(getField("Revision", fields));
		String views = getField("View", fields);
		for(String view : views.split("\n")) {
			label.addView(view);
		}
		return label;
	}

	/* (non-Javadoc)
	 * @see com.tek42.perforce.parse.Builder#getBuildCmd(java.lang.String)
	 */
	public String[] getBuildCmd(String id) {
		return new String[] { "p4", "label", "-o", id };
	}

	/* (non-Javadoc)
	 * @see com.tek42.perforce.parse.Builder#getSaveCmd()
	 */
	public String[] getSaveCmd() {
		return new String[] { "p4", "-s", "label", "-i" };
	}

	/* (non-Javadoc)
	 * @see com.tek42.perforce.parse.Builder#save(java.lang.Object, java.io.Writer)
	 */
	public void save(Label label, Writer writer) throws PerforceException {
		try {
			writer.write("Label: " + label.getName() + "\n");
			writer.write("Owner: " + label.getOwner() + "\n");
			writer.write("Description:\n\t" + label.getDescription() + "\n");
			writer.write("Revision: " + label.getRevision() + "\n");
			writer.write("Options: " + label.getOptions() + "\n");
			writer.write("View:\n");
			for(String view : label.getViews()) {
				writer.write("\t" + view + "\n");
			}
			writer.write("\n");
		} catch(IOException e) {
			throw new PerforceException("Failed to save label", e);
		}
	}

}
