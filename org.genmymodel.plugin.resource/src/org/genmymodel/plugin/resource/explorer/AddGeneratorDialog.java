package org.genmymodel.plugin.resource.explorer;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.genmymodel.common.api.GMMAPIRestClient;

public class AddGeneratorDialog extends TitleAreaDialog {
	protected Text nameInput, urlInput, branchInput;
	protected String name, url, branch;

	public AddGeneratorDialog(Shell parent, GMMAPIRestClient client) {
		super(parent);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Add new generator");
		setMessage("Please complete the informations below to add new generator.",
				IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2, false);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(layout);

		createGenerator(container);
		
		return area;
	}

	private void createGenerator(Composite container) {
		GridData labelGridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		labelGridData.widthHint = 150;
		GridData textGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		
		Label label = new Label(container, SWT.NONE);
		label.setText("Generator name");
		label.setLayoutData(labelGridData);
		nameInput = new Text(container, SWT.BORDER);
		nameInput.setText("New generator");
		nameInput.setLayoutData(textGridData);
		
		label = new Label(container, SWT.NONE);
		label.setText("Github url");
		label.setLayoutData(labelGridData);
		urlInput = new Text(container, SWT.BORDER);
		urlInput.setLayoutData(textGridData);
		urlInput.setText("https://github_url.git");
		
		label = new Label(container, SWT.NONE);
		label.setText("Github branch");
		label.setLayoutData(labelGridData);
		branchInput = new Text(container, SWT.BORDER);
		branchInput.setText("master");
		branchInput.setLayoutData(textGridData);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}
	
	@Override
	protected void okPressed() {
		name = nameInput.getText();
		url = urlInput.getText();
		branch = branchInput.getText();
		super.okPressed();
	}
	
	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public String getBranch() {
		return branch;
	}
	
	public boolean isValid() {
		return getUrl() != null 
				&& !getUrl().trim().isEmpty()
				&& getName() != null
				&& !getName().trim().isEmpty();
	}
	
	public boolean hasBranch() {
		return getBranch() != null && !getBranch().trim().isEmpty();
	}
}
