package org.genmymodel.engine.connector.jobs;

import org.eclipse.core.runtime.jobs.Job;
import org.genmymodel.engine.connector.project.IGenMyModelProject;

public abstract class GMMCustomGenJob extends Job {
	IGenMyModelProject project;
	
	public GMMCustomGenJob(String name, IGenMyModelProject project) {
		super(name);
		this.project = project;
	}
}
