package org.genmymodel.customgen.jobs;

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
import org.genmymodel.customgen.Activator;
import org.genmymodel.customgen.handlers.GMMAbstractHandler;
import org.genmymodel.customgen.project.GenMyModelProject;

/**
 * This class represents a GenMyModel job.
 * 
 * @author Vincent Aranega
 *
 */
public abstract class GMMCustomGenJob extends Job {
	GenMyModelProject project;

	/**
	 * Basic constructor.
	 * @param name The job name.
	 * @param project The GenMyModel project.
	 */
	public GMMCustomGenJob(String name, GenMyModelProject project) {
		super(name);
		this.project = project;
	}

	/**
	 * {@inheritDoc}
	 * Launches a GenMyModel job.
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask(GMMJobMessages.TASK_COMPEXEC, 7);
		monitor.subTask(GMMJobMessages.TASK_PREPAREZIP);
		File zip = null;
		try {
			zip = project.zipMe();
		} catch (Exception e) {
			e.printStackTrace();
			return blockError(String.format(GMMJobMessages.ERROR_ZIPPREP,
					GMMAbstractHandler.systemTmpFolder), e);
		}
		monitor.worked(1);

		if (zip == null) {
			return Status.OK_STATUS;
		}

		IStatus subw = apiCall(zip, monitor);

		monitor.subTask(GMMJobMessages.TASK_CLEANTMP);
		try {
			FileUtils.forceDelete(zip.getParentFile());
		} catch (IOException e) {
			nonBlockWarning(String.format(GMMJobMessages.ERROR_DELETE, zip
					.getParentFile().getAbsoluteFile()), e);
		}
		monitor.worked(1);

		if (subw != Status.OK_STATUS) {
			return subw;
		}

		try {
			project.getIProject().refreshLocal(IResource.DEPTH_INFINITE,
					new NullProgressMonitor());
		} catch (CoreException e) {
			return blockError(GMMJobMessages.ERROR_REFRESH, e);
		}

		return Status.OK_STATUS;
	}

	/**
	 * This method represent the call made to the API.
	 * @param zip The zip file to send to the API.
	 * @param monitor The current monitor.
	 * @return An IStatus exposing if yes or no the call was successful.
	 */
	protected abstract IStatus apiCall(File zip, IProgressMonitor monitor);

	/**
	 * Displays a blocking error message.
	 * @param message The message to display.
	 * @param e The exception that activates the error.
	 * @return Status.CANCEL_STATUS.
	 */
	IStatus blockError(String message, Throwable e) {
		IStatus err = new Status(Status.ERROR, Activator.PLUGIN_ID,
				Status.ERROR, message, e);
		StatusManager.getManager().handle(err, StatusManager.BLOCK);
		return Status.CANCEL_STATUS;
	}

	/**
	 * Displays a blocking warning message.
	 * @param message The message to display.
	 * @param e The exception that activates the warning.
	 * @return Status.CANCEL_STATUS.
	 */
	IStatus blockWarning(String message, Throwable e) {
		IStatus err = new Status(Status.WARNING, Activator.PLUGIN_ID,
				Status.WARNING, message, e);
		StatusManager.getManager().handle(err, StatusManager.BLOCK);
		return Status.CANCEL_STATUS;
	}

	/**
	 * Displays a non blocking warning message.
	 * @param message The message to display.
	 * @param e The exception that activates the warning.
	 * @return Status.OK_STATUS.
	 */
	IStatus nonBlockWarning(String message, Throwable e) {
		IStatus err = new Status(Status.WARNING, Activator.PLUGIN_ID,
				Status.OK, message, e);
		StatusManager.getManager().handle(err, StatusManager.SHOW);
		return Status.OK_STATUS;
	}

	/**
	 * Displays a non blocking error message.
	 * @param message The message to display.
	 * @param e The exception that activates the warning.
	 * @return Status.OK_STATUS.
	 */
	IStatus nonBlockError(String message, Throwable e) {
		IStatus err = new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK,
				message, e);
		StatusManager.getManager().handle(err, StatusManager.SHOW);
		return Status.OK_STATUS;
	}

	/**
	 * Displays a non blocking nested error mesage.
	 * @param errors A map of errors.
	 * @return Status.CANCEL_STATUS.
	 */
	IStatus nonblockError(Map<String, List<String>> errors) {
		MultiStatus err = new MultiStatus(Activator.PLUGIN_ID, Status.ERROR,
				"Error during project compilation", null);

		for (Entry<String, List<String>> entry : errors.entrySet()) {
			MultiStatus lab = new MultiStatus(Activator.PLUGIN_ID,
					Status.ERROR, entry.getKey(), null);
			for (String s : entry.getValue()) {
				lab.add(new Status(Status.ERROR, Activator.PLUGIN_ID,
						Status.ERROR, s, null));
			}
			err.add(lab);
		}

		StatusManager.getManager().handle(err, StatusManager.SHOW);
		return Status.CANCEL_STATUS;
	}
}
