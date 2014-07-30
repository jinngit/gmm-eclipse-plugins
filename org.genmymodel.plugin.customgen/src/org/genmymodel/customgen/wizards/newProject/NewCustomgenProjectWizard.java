package org.genmymodel.customgen.wizards.newProject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * Custom generator new project wizard.
 * 
 * @author Vincent Aranega
 *
 */
public class NewCustomgenProjectWizard extends Wizard implements INewWizard {
	NewCustomgenProjectWizardModel model;

	public NewCustomgenProjectWizard() {
		super();
		model = new NewCustomgenProjectWizardModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		addPage(new NewCustomgenProjectWizardPage());
	}

	/**
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canFinish() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		try {
			model.createProject();
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return true;
	}
}
