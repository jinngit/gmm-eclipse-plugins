package org.genmymodel.engine.connector.jobs;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.statushandlers.StatusManager;
import org.genmymodel.engine.connector.Activator;
import org.genmymodel.engine.connector.handlers.GMMAbstractHandler;
import org.genmymodel.engine.connector.project.GenMyModelProject;

public abstract class GMMCustomGenJob extends Job {
	GenMyModelProject project;
	
	public GMMCustomGenJob(String name, GenMyModelProject project) {
		super(name);
		this.project = project;
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Zipping custom generator project", 7);
		File zip = null;
		try {
			zip = project.zipMe();
		} catch (Exception e) {
			return blockError(
					"Error while zipping your projet! Did you have right to write in '"
							+ GMMAbstractHandler.systemTmpFolder
							+ "' tmp folder?", e);
		}
		monitor.worked(1);

		if (zip == null) {
			return Status.OK_STATUS;
		}
		
		IStatus subw = apiCall(zip, monitor);
		
		monitor.subTask("Cleaning tmp folders");
		try {
			FileUtils.forceDelete(zip.getParentFile());
		} catch (IOException e) {
			nonBlockWarning("Cannot delete '"
					+ zip.getParentFile().getAbsolutePath()
					+ "' temp directory. You should delete it by yourself.", e);
		}
		monitor.worked(1);
		
		if (subw != Status.OK_STATUS) {
			return subw;
		}
		
		try {
			project.getIProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (CoreException e) {
			return blockError(
					"Error while refreshing your project.",
					e);
		}

		return Status.OK_STATUS;
	}
	
	protected abstract IStatus apiCall(File zip, IProgressMonitor monitor);

	IStatus blockError(String message, Throwable e) {
		IStatus err = new Status(Status.ERROR, Activator.PLUGIN_ID, Status.ERROR,
				message, e);
		StatusManager.getManager().handle(err, StatusManager.BLOCK);
		return Status.CANCEL_STATUS;
	}

	IStatus blockWarning(String message, Throwable e) {
		IStatus err = new Status(Status.WARNING, Activator.PLUGIN_ID,
				Status.WARNING, message, e);
		StatusManager.getManager().handle(err, StatusManager.BLOCK);
		return Status.CANCEL_STATUS;
	}

	IStatus nonBlockWarning(String message, Throwable e) {
		IStatus err = new Status(Status.WARNING, Activator.PLUGIN_ID,
				Status.OK, message, e);
		StatusManager.getManager().handle(err, StatusManager.SHOW);
		return Status.OK_STATUS;
	}

	IStatus nonBlockError(String message, Throwable e) {
		IStatus err = new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK,
				message, e);
		StatusManager.getManager().handle(err, StatusManager.SHOW);
		return Status.OK_STATUS;
	}
	
	IStatus nonblockError(Map<String, List<String>> errors) {
		MultiStatus err = new MultiStatus(Activator.PLUGIN_ID, Status.ERROR,
				"Error during project compilation", null);
		
		for (Entry<String, List<String>> entry : errors.entrySet()) {
			MultiStatus lab = new MultiStatus(Activator.PLUGIN_ID, Status.ERROR, entry.getKey(), null);
			for (String s : entry.getValue()) {
				lab.add(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.ERROR, s, null));
			}
			err.add(lab);
		}
		
		StatusManager.getManager().handle(err, StatusManager.SHOW);
		return Status.CANCEL_STATUS;
	}
}
