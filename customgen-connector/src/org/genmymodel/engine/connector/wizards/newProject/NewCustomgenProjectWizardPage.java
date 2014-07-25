package org.genmymodel.engine.connector.wizards.newProject;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;


public class NewCustomgenProjectWizardPage extends WizardPage
{
	IWorkbench workbench;
	IStructuredSelection selection;
	Text nameWidget;
	Button metamodelWidget, transformationWidget;
	String name;
	boolean metamodel, transformation;
	NewCustomgenProjectWizardModel model;

	/**
	 * Constructor for ProjectMainPage.
	 */
	public NewCustomgenProjectWizardPage(IWorkbench workbench, IStructuredSelection selection) {
		super("Page");
		setTitle("GenMyModel Custom Generator Project");
		setDescription("Create new Project");
		this.workbench = workbench;
		this.selection = selection;	
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent)
	{		
		model =  ((NewCustomgenProjectWizard)getWizard()).model;
		Composite composite =  new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		setControl(composite);

		FormData fd = new FormData();

		Composite compositeField =  new Composite(composite, SWT.NULL);
		compositeField.setLayout(new FormLayout());
		setControl(compositeField);

		final Label l1 = new Label(compositeField, SWT.RIGHT);
		l1.setText("Project name:");
		fd.top = new FormAttachment(10, 10);
		fd.left = new FormAttachment(0, 10);
		fd.bottom = new FormAttachment(30, 0);
		fd.right = new FormAttachment(40, 0);
		l1.setLayoutData(fd);

		nameWidget = new Text(compositeField, SWT.BORDER | SWT.SINGLE);
		fd = new FormData();
		fd.top = new FormAttachment(l1, 0, SWT.TOP);
		fd.left = new FormAttachment(l1, 10);
		nameWidget.setLayoutData(fd);

		metamodelWidget = new Button(compositeField, SWT.CHECK);
		metamodelWidget.setText("My project will use intermediate metamodels");
		fd = new FormData();
		fd.top = new FormAttachment(l1, 5);
		fd.left = new FormAttachment(0, 10);
		fd.bottom = new FormAttachment(40, 0);
		fd.right = new FormAttachment(40, 0);
		metamodelWidget.setLayoutData(fd);

		transformationWidget = new Button(compositeField, SWT.CHECK);
		transformationWidget.setText("My project will use M2M transformations");
		fd = new FormData();
		fd.top = new FormAttachment(metamodelWidget, 0, SWT.TOP);
		fd.left = new FormAttachment(metamodelWidget, 10);
		transformationWidget.setLayoutData(fd);

		addListeners();
	}

	private void addListeners() {
		nameWidget.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				model.name = nameWidget.getText();

			}
		});

		metamodelWidget.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				model.metamodel = metamodelWidget.getSelection();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		transformationWidget.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				model.transformation = transformationWidget.getSelection();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}
}

