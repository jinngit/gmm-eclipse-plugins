package org.genmymodel.engine.connector.jobs;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.statushandlers.StatusManager;
import org.genmymodel.engine.connector.Activator;
import org.genmymodel.engine.connector.project.IGenMyModelProject;

public abstract class GMMCustomGenJob extends Job {
	IGenMyModelProject project;
	
	public GMMCustomGenJob(String name, IGenMyModelProject project) {
		super(name);
		this.project = project;
	}
	

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
	
	IStatus blockError(Map<String, List<String>> errors) {
		MultiStatus err = new MultiStatus(Activator.PLUGIN_ID, Status.ERROR,
				"Error during project compilation", null);
		
		for (Entry<String, List<String>> entry : errors.entrySet()) {
			MultiStatus lab = new MultiStatus(Activator.PLUGIN_ID, Status.ERROR, entry.getKey(), null);
			for (String s : entry.getValue()) {
				lab.add(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.ERROR, s, null));
			}
			err.add(lab);
		}
		
		StatusManager.getManager().handle(err, StatusManager.BLOCK);
		return Status.CANCEL_STATUS;
	}
}
