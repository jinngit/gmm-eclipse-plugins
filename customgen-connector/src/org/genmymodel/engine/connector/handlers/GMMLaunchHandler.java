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
 * This class provides handler calling GenMyModel API. This handler creates
 * a launch configuration and shows it in the launch configuration dialog.
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
	 * {@inheritDoc} Opens the launch configuration dialog with a pre-configured
	 * launch configuration.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		super.execute(event);

		try {
			IWorkbenchPart part = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage().getActivePart();
			ILaunchManager manager = DebugPlugin.getDefault()
					.getLaunchManager();
			ILaunchConfigurationType type = manager
					.getLaunchConfigurationType(GMMRunconfigConstant.GENMYMODEL_RUNCONF_TYPE);
			ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(
					null, "New Configuration (" + java.util.UUID.randomUUID()
							+ ")");

			workingCopy.setAttribute(GMMRunconfigConstant.CUSTOMGEN_PROJECT,
					getGMMProject().getIProject().getName());
			workingCopy
					.setModes(Collections.singleton(ILaunchManager.RUN_MODE));
			ILaunchConfiguration configuration = workingCopy.doSave();

			DebugUITools.openLaunchConfigurationDialog(part.getSite()
					.getShell(), configuration,
					"org.eclipse.debug.ui.launchGroup.run", null);
		} catch (CoreException e) {
		}

		return null;
	}
}
