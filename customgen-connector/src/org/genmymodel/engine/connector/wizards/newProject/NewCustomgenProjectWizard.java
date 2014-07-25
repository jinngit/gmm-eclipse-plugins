package org.genmymodel.engine.connector.wizards.newProject;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

public class NewCustomgenProjectWizard extends Wizard implements INewWizard
{	
	NewCustomgenProjectWizardPage projectPage;
	NewCustomgenProjectWizardModel model;
	protected IStructuredSelection selection;
	protected IWorkbench workbench;

	public NewCustomgenProjectWizard() {
		super();
		model = new NewCustomgenProjectWizardModel();
	}
	
	public void addPages()
	{
		projectPage = new NewCustomgenProjectWizardPage(workbench, selection);
		addPage(projectPage);
	}

	/**
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) 
	{
		// TODO : Update this part - Folder
		this.workbench = workbench;
		this.selection = selection;
		if (selection != null && !selection.isEmpty()) {
			Object obj = selection.getFirstElement();
			if (obj  instanceof IFolder) {
				System.out.println("Folder !!");				
			}
		}
	}

	public boolean canFinish()
	{
		// complete the wizard from the first page
		if (this.getContainer().getCurrentPage() == projectPage) 
			return true;
		return false;
	}
	
	public boolean performFinish() 
	{
		try {
			model.createProject();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return true;
	}
}
