package org.genmymodel.engine.connector.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.Job;
import org.genmymodel.engine.connector.jobs.GMMCompileJob;

/**
 * This class provides handler calling GenMyModel API. The GenMyModel service
 * called allows one to compile its project.
 * 
 * @author Vincent Aranega
 *
 */
public class GMMCompileHandler extends GMMAbstractHandler {

	/**
	 * The constructor.
	 */
	public GMMCompileHandler() {}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		super.execute(event);
		
		/*
		ISelection selection = HandlerUtil.getCurrentSelection(getCurrentEvent());
		IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart(); 
		
		System.out.println("getGMMProject() "+ getGMMProject());
		GenMyModelProject project = getGMMProject();
		ProjectWizard wizard = new ProjectWizard(project);
		if ((selection instanceof IStructuredSelection) || (selection == null))
		wizard.init(part.getSite().getWorkbenchWindow().getWorkbench(), 
			(IStructuredSelection)selection);
			
		WizardDialog dialog = new WizardDialog( part.getSite().getShell(), wizard);
		dialog.create();
		dialog.open();
		*/
		Job compile = new GMMCompileJob("Custom generator compilation", getGMMProject());
		compile.schedule();
		
		return null;
	}
}
;