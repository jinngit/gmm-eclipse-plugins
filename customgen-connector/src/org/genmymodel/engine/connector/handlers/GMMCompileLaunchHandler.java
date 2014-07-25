package org.genmymodel.engine.connector.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.genmymodel.engine.connector.api.GMMAPIRestClient;
import org.genmymodel.engine.connector.api.GMMCredential;
import org.genmymodel.engine.connector.api.ProjectBinding;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class GMMCompileLaunchHandler extends GMMAbstractHandler {
	/**
	 * The constructor.
	 */
	public GMMCompileLaunchHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ProjectBinding[] projects = GMMAPIRestClient.getInstance().GETMyProjects(new GMMCredential("","")); //TODO
		for (ProjectBinding p : projects) {
			System.out.println(p.getName());
			System.out.println(p.getProjectId());
			System.out.println(p.isPublic());
			System.out.println();
		}
		return null;
	}
}
