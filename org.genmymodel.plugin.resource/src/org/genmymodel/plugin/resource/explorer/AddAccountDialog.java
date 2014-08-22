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

/**
 * 
 * @author Ali Gourch
 */
public class AddAccountDialog extends TitleAreaDialog {
	private Text usernameInput, passwordInput;
	private String username, password;

	public AddAccountDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Add account");
		setMessage("Please insert your username and password.",
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

		createUsername(container);
		createPassword(container);

		return area;
	}

	private void createUsername(Composite container) {
		Label label = new Label(container, SWT.NONE);
		label.setText("Username");

		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;

		usernameInput = new Text(container, SWT.BORDER);
		usernameInput.setLayoutData(data);
	}

	private void createPassword(Composite container) {
		Label label = new Label(container, SWT.NONE);
		label.setText("Password");

		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		passwordInput = new Text(container, SWT.BORDER | SWT.PASSWORD);
		passwordInput.setLayoutData(data);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {
		username = usernameInput.getText();
		password = passwordInput.getText();
		super.okPressed();
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}
