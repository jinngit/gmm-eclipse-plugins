package org.genmymodel.plugin.resource.explorer;

import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.genmymodel.common.account.GMMCredential;
import org.genmymodel.common.api.CustomGeneratorBinding;
import org.genmymodel.common.api.GMMAPIRestClient;
import org.springframework.http.ResponseEntity;

/**
 * 
 * @author Ali Gourch
 * @author Vincent Aranega
 * 
 */
public class GenerationDialog extends TitleAreaDialog {
	private Combo generatorCombo;
	private CustomGeneratorBinding generator;
	private Text destinationInput;
	private IContainer destination;
	private Button addButton, deleteButton, destinationButton, modifyButton;
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
		GridLayout layout = new GridLayout(5, false);
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
		generatorCombo.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!modifyButton.isEnabled()) {
					modifyButton.setEnabled(true);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (modifyButton.isEnabled()) {
					modifyButton.setEnabled(false);
				}
				
			}
		});
		generatorCombo.setLayoutData(data);

		addButton = new Button(container, SWT.BORDER);
		addButton.setText("    Add    ");
		addButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AddGeneratorDialog dialog = new AddGeneratorDialog(container.getShell(), client);
				dialog.open();
				
				if (dialog.getReturnCode() == Window.OK && dialog.isValid()) {
					CustomGeneratorBinding customgen = new CustomGeneratorBinding();
					customgen.setName(dialog.getName());
					customgen.setGeneratorURL(dialog.getUrl());
					if (dialog.hasBranch()) {
						customgen.setGeneratorBranch(dialog.getBranch());
					}
					ResponseEntity<CustomGeneratorBinding> result = client.POSTGenerator(credential, customgen); // TODO manage error?
					generatorCombo.add(result.getBody().getName());
					generatorCombo.setData(result.getBody().getName(), result.getBody());
					generatorCombo.select(generatorCombo.indexOf(result.getBody().getName()));
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		modify(container, credential);

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
		destinationButton = new Button(container, SWT.BORDER);
		destinationButton.setText("Browse...");
		destinationButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SelectionDialog dialog = new ContainerSelectionDialog(getParentShell(), ResourcesPlugin.getWorkspace().getRoot(), true, "Select a generation container.");
				dialog.setTitle("Browse...");
				dialog.open();
				if (dialog.getResult() != null && dialog.getResult().length > 0
						&& dialog.getResult()[0] instanceof IPath) {
					IPath selectedPath = (IPath)dialog.getResult()[0];
					destinationInput.setText(selectedPath.toString());
					if (selectedPath.segmentCount() == 1) {
						destinationInput.setData(ResourcesPlugin.getWorkspace().getRoot().getProject(selectedPath.lastSegment()));
					} else {
						destinationInput.setData(ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(selectedPath));
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		destinationButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
				false, false));
	}
	
	private void modify(final Composite container, final GMMCredential credential) {
		modifyButton = new Button(container, SWT.BORDER);
		modifyButton.setText("    Modify    ");
		modifyButton.setEnabled(false);
		modifyButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ModifyGeneratorDialog dialog = new ModifyGeneratorDialog(container.getShell(), client, (CustomGeneratorBinding)generatorCombo.getData(generatorCombo.getText()));
				dialog.open();
				
				if (dialog.getReturnCode() == Window.OK && dialog.isValid()) {
					try {
						client.PUTCustomGen(dialog.getGenerator(), credential);
					} catch (IOException e1) {
						e1.printStackTrace(); // TODO manage errors
					}
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {
		destination = (IContainer)destinationInput.getData();
		generator = (CustomGeneratorBinding) generatorCombo.getData(generatorCombo.getText());
		super.okPressed();
	}

	public CustomGeneratorBinding getGenerator() {
		return generator;
	}

	public IContainer getDestination() {
		return destination;
	}
}