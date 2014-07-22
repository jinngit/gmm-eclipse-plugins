package org.genmymodel.engine.connector.jobs;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;
import org.genmymodel.engine.connector.Activator;
import org.genmymodel.engine.connector.api.GMMAPIRestClient;
import org.genmymodel.engine.connector.handlers.GMMAbstractHandler;
import org.genmymodel.engine.connector.project.IGenMyModelProject;

public class GMMCompileJob extends GMMCustomGenJob {

	public GMMCompileJob(String name, IGenMyModelProject project) {
		super(name, project);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Zipping custom generator project", 4);
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
			return Status.CANCEL_STATUS;
		}
		monitor.worked(1);

		monitor.subTask("Calling GenMyModel API compilation URL");
		GMMAPIRestClient.getInstance().POSTCompile(zip);
		monitor.worked(2);

		monitor.subTask("Cleaning tmp folders");
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
		monitor.worked(1);

		return Status.OK_STATUS;
	}


}
