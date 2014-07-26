package org.genmymodel.engine.connector.wizards.compile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.genmymodel.engine.connector.jobs.GMMCompileJob;
import org.genmymodel.engine.connector.project.GenMyModelProject;

public class ProjectWizard extends Wizard implements INewWizard
{	
	protected ProjectMainPage projectPage;
	protected ProjectModel model;
	protected IStructuredSelection selection;
	protected IWorkbench workbench;
	GenMyModelProject project;

	public ProjectWizard(GenMyModelProject project) {
		super();
		model = new ProjectModel();
		this.project = project;
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
		System.out.println("dsfsdf"+model.getCredential());
		Job job = new GMMCompileJob("Compilation process", project);
		job.schedule();
		
		return true;
	}
}
