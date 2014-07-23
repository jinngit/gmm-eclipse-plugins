package org.genmymodel.engine.connector.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.Job;
import org.genmymodel.engine.connector.jobs.GMMLaunchJob;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
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
