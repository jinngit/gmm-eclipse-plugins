package org.genmymodel.customgen.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.Job;
import org.genmymodel.customgen.jobs.GMMCompileJob;

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
	 * {@inheritDoc} Launches the compilation process.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		super.execute(event);

		Job compile = new GMMCompileJob("Custom generator compilation",
				getGMMProject());
		compile.schedule();

		return null;
	}
};
