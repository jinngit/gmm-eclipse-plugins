package org.genmymodel.engine.connector.handlers;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;
import org.genmymodel.engine.connector.Activator;
import org.genmymodel.engine.connector.api.GMMAPIRestClient;
import org.genmymodel.engine.connector.project.IGenMyModelProject;

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
	public GMMCompileHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		super.execute(event);

		IGenMyModelProject project = getGMMProject();

		File zip = null;
		try {
			zip = project.zipMe();
		} catch (Exception e) {
			IStatus error = new Status(
					Status.ERROR, 
					Activator.PLUGIN_ID, 
					Status.OK, 
					"Error while zipping your projet! Did you have right to write in '" + GMMAbstractHandler.systemTmpFolder + "' tmp folder?", 
					e);
			StatusManager.getManager().handle(error, StatusManager.BLOCK);
			return null;
		}

		GMMAPIRestClient.getInstance().POSTCompile(zip);
		
		IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(window.getShell(),
				"GenMyModel Engine Connector", "Zip is OK");
		
		try {
			FileUtils.forceDelete(zip.getParentFile());
		} catch (IOException e) {
			IStatus warn = new Status(
					Status.WARNING, 
					Activator.PLUGIN_ID, 
					Status.OK, 
					"Cannot delete '" + zip.getParentFile().getAbsolutePath() + "' temp directory. You should delete it by yourself.", 
					e);
			StatusManager.getManager().handle(warn, StatusManager.SHOW);
		}

		return null;
	}

}
