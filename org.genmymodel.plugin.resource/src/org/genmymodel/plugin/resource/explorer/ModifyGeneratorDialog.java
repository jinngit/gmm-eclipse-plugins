package org.genmymodel.plugin.resource.explorer;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.genmymodel.common.api.CustomGeneratorBinding;
import org.genmymodel.common.api.GMMAPIRestClient;

public class ModifyGeneratorDialog extends AddGeneratorDialog {
	protected CustomGeneratorBinding generator;
	protected boolean hasChanged = false;

	public ModifyGeneratorDialog(Shell parent, GMMAPIRestClient client, CustomGeneratorBinding generator) {
		super(parent, client);
		this.generator = generator;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Modify " + generator.getName() + " custom generator");
		setMessage("Please complete the informations below to modify the generator.",
				IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Control area = super.createDialogArea(parent);
		nameInput.setText(generator.getName());
		urlInput.setText(generator.getGeneratorURL());
		branchInput.setText(generator.getGeneratorBranch());
		return area;
	}
	
	public CustomGeneratorBinding getGenerator() {
		return this.generator;
	}
	
	@Override
	protected void okPressed() {
		super.okPressed();
		if (!generator.getName().equalsIgnoreCase(name)) {
			generator.setName(name);
			hasChanged = true;
		}
		if (!generator.getGeneratorURL().equalsIgnoreCase(url)) {
			generator.setGeneratorURL(url);
			hasChanged = true;
		}
		if (!generator.getGeneratorBranch().equalsIgnoreCase(branch)) {
			generator.setGeneratorBranch(branch);
			hasChanged = true;
		}
	}
	
	@Override
	public boolean isValid() {
		return super.isValid() && this.hasChanged;
	}
	
}
