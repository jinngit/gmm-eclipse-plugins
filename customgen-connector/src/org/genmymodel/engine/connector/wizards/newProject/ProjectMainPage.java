package org.genmymodel.engine.connector.wizards.newProject;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;


public class ProjectMainPage extends WizardPage implements Listener
{
	IWorkbench workbench;
	IStructuredSelection selection;
	Text name, password;
	Button metamodel, transformation;
	
	/**
	 * Constructor for ProjectMainPage.
	 */
	public ProjectMainPage(IWorkbench workbench, IStructuredSelection selection) {
		super("Page");
		setTitle("Project");
		setDescription("Create new Project :");
		this.workbench = workbench;
		this.selection = selection;	
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent)
	{		
		Composite composite =  new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		setControl(composite);
		
		FormData fd = new FormData();
		
		Composite compositeField =  new Composite(composite, SWT.NULL);
		compositeField.setLayout(new FormLayout());
		setControl(compositeField);
		
		final Label l1 = new Label(compositeField, SWT.RIGHT);
	    l1.setText("Name :");
	    fd.top = new FormAttachment(10, 10);
	    fd.left = new FormAttachment(0, 10);
	    fd.bottom = new FormAttachment(30, 0);
	    fd.right = new FormAttachment(40, 0);
	    l1.setLayoutData(fd);
	    
	    name = new Text(compositeField, SWT.BORDER | SWT.SINGLE);
	    fd = new FormData();
	    fd.top = new FormAttachment(l1, 0, SWT.TOP);
	    fd.left = new FormAttachment(l1, 10);
	    name.setLayoutData(fd);
	    
	    metamodel = new Button(compositeField, SWT.CHECK);
	    metamodel.setText("Metamodel");
	    fd = new FormData();
	    fd.top = new FormAttachment(l1, 5);
	    fd.left = new FormAttachment(0, 10);
	    fd.bottom = new FormAttachment(40, 0);
	    fd.right = new FormAttachment(40, 0);
	    metamodel.setLayoutData(fd);
	    
	    transformation = new Button(compositeField, SWT.CHECK);
	    transformation.setText("Transformation");
	    fd = new FormData();
	    fd.top = new FormAttachment(metamodel, 0, SWT.TOP);
	    fd.left = new FormAttachment(metamodel, 10);
	    transformation.setLayoutData(fd);
	    
	    addListeners();
	}
	
	private void addListeners() {
		name.addListener(SWT.Selection, this);
		metamodel.addListener(SWT.Selection, this);
		transformation.addListener(SWT.Selection, this);
		
//		transformation.addSelectionListener(new SelectionAdapter()
//	    {
//	        @Override
//	        public void widgetSelected(SelectionEvent e)
//	        {
//	            if (transformation.getSelection())
//	            {
//	                System.out.println(true);
//	                transformation.set
//	            }
//	            else
//	            	System.out.println(false);
//	        }
//	    });
	}
	
	private void saveDataToModel()
	{
		ProjectWizard wizard = (ProjectWizard)getWizard();
		ProjectModel model = wizard.model;
		model.name = name.getText();
		model.metamodel = metamodel.getSelection();
		model.transformation = transformation.getSelection();
	}
	
	 @Override
     public void handleEvent(Event event) {
         saveDataToModel();
     }
}

