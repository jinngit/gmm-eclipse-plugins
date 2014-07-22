package org.genmymodel.engine.connector.wizards;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class ProjectWizard extends Wizard implements INewWizard
{	
	ProjectMainPage projectPage;
	ProjectModel model;
	protected IStructuredSelection selection;
	protected IWorkbench workbench;

	public ProjectWizard() {
		super();
		model = new ProjectModel();
	}
	
	public void addPages()
	{
		projectPage = new ProjectMainPage(workbench, selection);
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
		String summary = model.toString();
		MessageDialog.openInformation(workbench.getActiveWorkbenchWindow().getShell(), 
			"Project info", summary);
		return true;
	}
}
