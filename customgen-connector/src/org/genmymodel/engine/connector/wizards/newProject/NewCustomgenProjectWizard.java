package org.genmymodel.engine.connector.wizards.newProject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

public class NewCustomgenProjectWizard extends Wizard implements INewWizard {
	NewCustomgenProjectWizardModel model;

	public NewCustomgenProjectWizard() {
		super();
		model = new NewCustomgenProjectWizardModel();
	}

	public void addPages() {
		addPage(new NewCustomgenProjectWizardPage());
	}

	/**
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}

	public boolean canFinish() {
		return true;
	}

	public boolean performFinish() {
		try {
			model.createProject();
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return true;
	}
}
