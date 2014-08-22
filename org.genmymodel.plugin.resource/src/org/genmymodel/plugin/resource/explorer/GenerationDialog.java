package org.genmymodel.plugin.resource.explorer;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.genmymodel.common.account.GMMCredential;
import org.genmymodel.common.api.CustomGeneratorBinding;
import org.genmymodel.common.api.GMMAPIRestClient;
import org.springframework.http.ResponseEntity;

/**
 * 
 * @author Ali Gourch
 */
public class GenerationDialog extends TitleAreaDialog {
	private Combo generatorCombo;
	private CustomGeneratorBinding generator;
	private DirectoryDialog destinationDirectory;
	private Text destinationInput;
	private String destination;
	private Button addButton, deleteButton, destiontionButton;
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
		GridLayout layout = new GridLayout(4, false);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(layout);

		generators(container);
		destination(container);

		return area;
	}

	private void generators(final Composite container) {
		Label label = new Label(container, SWT.NONE);
		label.setText("Generator");

		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;

		generatorCombo = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		final GMMCredential credential = ((TreeObject) ((IStructuredSelection) viewer
				.getSelection()).getFirstElement()).getCredential();
		CustomGeneratorBinding[] customGenerators = client
				.GETMyCustomGenerators(credential);
		for (CustomGeneratorBinding customGenerator : customGenerators) {
			generatorCombo.add(customGenerator.getName());
			generatorCombo.setData(customGenerator.getName(), customGenerator);
		}
		generatorCombo.setLayoutData(data);

		addButton = new Button(container, SWT.BORDER);
		addButton.setText("    Add    ");
		addButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AddGeneratorDialog dialog = new AddGeneratorDialog(container.getShell(), client);
				dialog.open();
				String name = dialog.getName();
				String url = dialog.getUrl();
				String branch = dialog.getBranch();
				if (name != "" && url != "" && branch != "") {
					CustomGeneratorBinding customgen = new CustomGeneratorBinding();
					customgen.setName(name);
					customgen.setGeneratorURL(url);
					customgen.setGeneratorBranch(branch);
					ResponseEntity<CustomGeneratorBinding> result = client.POSTGenerator(credential, customgen);
					generatorCombo.add(result.getBody().getName());
					generatorCombo.setData(result.getBody().getName(), result.getBody());
					generatorCombo.select(generatorCombo.indexOf(result.getBody().getName()));
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		deleteButton = new Button(container, SWT.BORDER);
		deleteButton.setText("    Delete    ");
		deleteButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (MessageDialog.openConfirm(container.getShell(), "Deleting generator", "Are you sure you want to delete this generator ?")) {
					client.DELETEGenerator(credential, Integer.parseInt(((CustomGeneratorBinding)generatorCombo.getData(generatorCombo.getText())).getGeneratorId()));
					generatorCombo.remove(generatorCombo.getSelectionIndex());
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void destination(final Composite container) {
		Label label = new Label(container, SWT.NONE);
		label.setText("Destination");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		destinationInput = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		destinationInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
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
					destinationInput.setText(dir);
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

	@Override
	protected void okPressed() {
		destination = destinationInput.getText();
		generator = (CustomGeneratorBinding) generatorCombo.getData(generatorCombo.getText());
		super.okPressed();
	}

	public CustomGeneratorBinding getGenerator() {
		return generator;
	}

	public String getDestination() {
		return destination;
	}
}