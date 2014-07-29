package org.genmymodel.engine.connector.runconfig;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.genmymodel.engine.connector.api.GMMCredential;
import org.genmymodel.engine.connector.jobs.GMMLaunchJob;
import org.genmymodel.engine.connector.project.GenMyModelProject;

/**
 * This class provides facilities to launch a custom generation execution.
 * 
 * @author Vincent Aranega
 * @author Ali Gourch
 *
 */
public class GMMRunconfigLaunchDelegate implements ILaunchConfigurationDelegate {

	/**
	 * {@inheritDoc}
	 * It simply call the already existing launch job.
	 */
	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		
		Map<String, Object> atts = configuration.getWorkingCopy().getAttributes();
		String login = (String)atts.get(GMMRunconfigConstant.LOGIN);
		String password = (String)atts.get(GMMRunconfigConstant.PASSWORD);
		String project = (String)atts.get(GMMRunconfigConstant.CUSTOMGEN_PROJECT);
		String modelID = (String)atts.get(GMMRunconfigConstant.MODEL_PROJECT);
		
		GMMCredential credential = new GMMCredential(login, password);
		
		IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(project);
		GMMLaunchJob job = new GMMLaunchJob(configuration.getName(), new GenMyModelProject(iProject), modelID, credential);
		job.schedule();
	}

}
