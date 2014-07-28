package org.genmymodel.engine.connector.handlers;

import java.util.Collections;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.genmymodel.engine.connector.runconfig.GMMRunconfigConstant;

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

		try {
			IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart(); 
			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType type = manager.getLaunchConfigurationType(GMMRunconfigConstant.GENMYMODEL_RUNCONF_TYPE);
			ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null, "New Configuration (" + java.util.UUID.randomUUID() +")");

			workingCopy.setAttribute(GMMRunconfigConstant.CUSTOMGEN_PROJECT, getGMMProject().getIProject().getName());
			workingCopy.setModes(Collections.singleton(ILaunchManager.RUN_MODE));
			ILaunchConfiguration configuration = workingCopy.doSave();
			
			DebugUITools.openLaunchConfigurationDialog(part.getSite().getShell(), configuration, "org.eclipse.debug.ui.launchGroup.run", null);
		} catch (CoreException e) {
		}

		return null;
	}
}
