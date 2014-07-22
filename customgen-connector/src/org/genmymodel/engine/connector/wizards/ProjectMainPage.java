package org.genmymodel.engine.connector.wizards;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbench;

public class ProjectMainPage extends WizardPage implements Listener
{
	IWorkbench workbench;
	IStructuredSelection selection;
	
	Combo project;
	Tree tree;
		
	final static String[] choices ={ "1", "jhkjhkj", "3", "4", "5", "6", "7", "8", "9", "10"};
	
	/**
	 * Constructor for ProjectMainPage.
	 */
	public ProjectMainPage(IWorkbench workbench, IStructuredSelection selection) {
		super("Page");
		setTitle("Project");
		setDescription("Select the project");
		this.workbench = workbench;
		this.selection = selection;	
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent)
	{		
		Composite composite =  new Composite(parent, SWT.NULL);
		GridLayout gl = new GridLayout();
		composite.setLayout(gl);
		setControl(composite);
		
		CTabFolder folder = new CTabFolder(composite, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		folder.setLayoutData(data);
		CTabItem cTabItem1 = new CTabItem(folder, SWT.BORDER);
		cTabItem1.setText("Mine");
		CTabItem cTabItem2 = new CTabItem(folder, SWT.BORDER);
		cTabItem2.setText("Public");
		
		Composite cTabItemComposite =  new Composite(folder, SWT.NULL);
		GridLayout cTabItemGl = new GridLayout();
		cTabItemComposite.setLayout(cTabItemGl);
		setControl(cTabItemComposite);
		
		Label label = new Label (cTabItemComposite, SWT.NONE);
		label.setText("List of projects :");		
		project = new Combo(cTabItemComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		project.setItems(choices);
		project.select(0);
		
//		tree = new Tree(project, SWT.SINGLE);
//		TreeItem tree_1 = new TreeItem(tree, SWT.NONE);
//		tree_1.setText("element 1");
//		TreeItem tree_2 = new TreeItem(tree, SWT.NONE);
//		tree_2.setText("element 2");
//		TreeItem tree_2_1 = new TreeItem(tree_2, SWT.NONE);
//		tree_2_1.setText("element 2 1");
//		TreeItem tree_2_2 = new TreeItem(tree_2, SWT.NONE);
//		tree_2_2.setText("element 2 2");
//		TreeItem tree_3 = new TreeItem(tree, SWT.NONE);
//		tree_3.setText("element 3");
//		tree.setSize(100, 100);

		cTabItem1.setControl(cTabItemComposite);		
		cTabItem2.setControl(cTabItemComposite);
		addListeners();
    }
	
	private void addListeners() {
		project.addListener(SWT.Selection, this);
	}
	
	private void saveDataToModel()
	{
		ProjectWizard wizard = (ProjectWizard)getWizard();
		ProjectModel model = wizard.model;
		model.projectChoice = project.getItem(project.getSelectionIndex());
	}
	
	 @Override
     public void handleEvent(Event arg0) {
         saveDataToModel();
     }
}

