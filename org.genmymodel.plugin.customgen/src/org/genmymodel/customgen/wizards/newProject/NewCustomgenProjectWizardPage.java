package org.genmymodel.customgen.wizards.newProject;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Ali Gourch
 * @author Vincent Aranega
 *
 */
public class NewCustomgenProjectWizardPage extends WizardPage {
	private Text nameWidget;
	private Button metamodelWidget;
	private Button transformationWidget;
	private NewCustomgenProjectWizardModel model;

	/**
	 * Constructor for ProjectMainPage.
	 */
	public NewCustomgenProjectWizardPage() {
		super("Page");
		setTitle("GenMyModel Custom Generator Project");
		setDescription("Create new Project");
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		model = ((NewCustomgenProjectWizard) getWizard()).model;
		
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		setControl(composite);
		
		Composite upper = new Composite(composite, SWT.NONE);
		upper.setLayout(new GridLayout(2, false));
		Label nameLabel = new Label(upper, SWT.RIGHT);
		nameLabel.setText("Project name:");
		
		nameWidget = new Text(upper, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		nameWidget.setLayoutData(gridData);

		Group optionsInfo = new Group(composite, SWT.NONE);
		optionsInfo.setSize(composite.getSize());
		optionsInfo.setText("custom generator options");
		optionsInfo.setLayout(new GridLayout(2, true));

		metamodelWidget = new Button(optionsInfo, SWT.CHECK);
		metamodelWidget.setText("My project will use intermediate metamodels");
		
		
		transformationWidget = new Button(optionsInfo, SWT.CHECK);
		transformationWidget.setText("My project will use M2M transformations");
		GridData gdata = new GridData();
		gdata.horizontalAlignment = SWT.RIGHT;
		gdata.grabExcessHorizontalSpace = true;
		transformationWidget.setLayoutData(gdata);
		
		addListeners();
	}

	/**
	 * Adds widget listeners.
	 */
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
