package org.genmymodel.plugin.resource.explorer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.statushandlers.StatusManager;
import org.genmymodel.common.account.GMMCredential;
import org.genmymodel.common.api.CustomGeneratorBinding;
import org.genmymodel.common.api.GMMAPIRestClient;
import org.genmymodel.plugin.resource.Activator;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;

/**
 * 
 * @author Ali Gourch
 */
public class GenerationDialog extends TitleAreaDialog {
	private Combo generatorCombo;
	private CustomGeneratorBinding generator;
	private DirectoryDialog destinationDirectory;
	private Text destinationText;
	private Button destiontionButton;
	private String destination;
	private GMMAPIRestClient client;
	private TreeViewer viewer;

	public GenerationDialog(TreeViewer viewer, GMMAPIRestClient client) {
		super(viewer.getControl().getShell());
		this.viewer = viewer;
		this.client = client;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Generation");
		setMessage("Please choose your generator and the destination.",
				IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(3, false);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(layout);

		createGenerators(container);
		createDestination(container);

		return area;
	}

	private void createGenerators(Composite container) {
		Label label = new Label(container, SWT.NONE);
		label.setText("Generator");

		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;

		generatorCombo = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		try {
			GMMCredential credential = ((TreeObject) ((IStructuredSelection) viewer
					.getSelection()).getFirstElement()).getCredential();
			CustomGeneratorBinding[] customGenerators = client
					.GETMyCustomGenerators(credential);
			for (CustomGeneratorBinding customGenerator : customGenerators) {
				generatorCombo.add(customGenerator.getName());
				generatorCombo.setData(customGenerator.getName(),
						customGenerator);
			}

		} catch (OAuth2AccessDeniedException e) {
			IStatus err = new Status(
					Status.ERROR,
					Activator.PLUGIN_ID,
					Status.ERROR,
					"Login/password error\n\tPlease verify your information and be sure that you set a passord for your account.",
					e);
			StatusManager.getManager().handle(err, StatusManager.BLOCK);
		}
		generatorCombo.select(0);
		generatorCombo.setLayoutData(data);
		new Label(container, SWT.NONE);
	}

	private void createDestination(final Composite container) {
		Label label = new Label(container, SWT.NONE);
		label.setText("Destination");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		destinationText = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		destinationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		destiontionButton = new Button(container, SWT.BORDER);
		destiontionButton.setText("Browse...");
		destiontionButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				destinationDirectory = new DirectoryDialog(
						container.getShell(), SWT.OPEN | SWT.BORDER
								| SWT.READ_ONLY);
				String dir = destinationDirectory.open();
				if (dir != null) {
					destinationText.setText(dir);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		destiontionButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
				false, false));
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	private void saveInput() {
		generator = (CustomGeneratorBinding) generatorCombo
				.getData(generatorCombo.getText());
		destination = destinationText.getText();
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public CustomGeneratorBinding getGenerator() {
		return generator;
	}

	public String getDestination() {
		return destination;
	}
}