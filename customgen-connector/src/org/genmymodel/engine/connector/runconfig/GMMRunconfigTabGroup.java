package org.genmymodel.engine.connector.runconfig;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class GMMRunconfigTabGroup extends AbstractLaunchConfigurationTabGroup {

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		 ILaunchConfigurationTab[] tabs = 
		            new ILaunchConfigurationTab[] {
				 		new GMMRunconfigTab(),
		                new CommonTab(),
		            };
		        setTabs(tabs);
	}

}
