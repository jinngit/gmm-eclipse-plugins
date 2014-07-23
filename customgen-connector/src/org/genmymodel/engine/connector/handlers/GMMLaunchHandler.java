package org.genmymodel.engine.connector.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.Job;
import org.genmymodel.engine.connector.jobs.GMMLaunchJob;

/**
 * This class provides handler calling GenMyModel API. The GenMyModel service
 * called allows one to launch its previously compiled project.
 * 
 * @author Vincent Aranega
 */
public class GMMLaunchHandler extends GMMAbstractHandler {
	/**
	 * The constructor.
	 */
	public GMMLaunchHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		super.execute(event);

		Job job = new GMMLaunchJob("Code generation process", getGMMProject());
		job.schedule();

		return null;
	}
}
