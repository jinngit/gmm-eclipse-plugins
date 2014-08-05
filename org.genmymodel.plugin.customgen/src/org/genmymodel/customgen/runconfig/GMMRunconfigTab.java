package org.genmymodel.customgen.runconfig;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;
import org.eclipse.ui.statushandlers.StatusManager;
import org.genmymodel.common.account.GMMCredential;
import org.genmymodel.common.api.GMMAPIRestClient;
import org.genmymodel.common.api.ProjectBinding;
import org.genmymodel.customgen.Activator;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;

/**
 * The custom generator run configuration tab in run configuration dialog.
 * 
 * @author Ali Gourch
 * @author Vincent Aranega
 *
 */
public class GMMRunconfigTab extends AbstractLaunchConfigurationTab {

	private static final String GID =  "org.genmymodel.runconfig.tab";
	private Table table;
	private Text login;
	private Text password;
	private Text project;
	private Button refresh;
	private Button browse;
	private String modelID;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createControl(Composite parent) {
		final Composite root = new Composite(parent, SWT.NULL);
		setControl(root);
		GridLayout grid = new GridLayout();
		grid.marginTop = 10;
		grid.verticalSpacing = 5;
		grid.numColumns = 1;
		root.setLayout(grid);

		Group projectSelection = new Group(root, SWT.NONE); // createGroup(root, "Custom generator project", 2, 1, GridData.FILL);
		projectSelection.setLayout(new GridLayout(2, false));
		projectSelection.setText("Custom generator project");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		projectSelection.setLayoutData(gd);
		project = new Text(projectSelection, SWT.SINGLE | SWT.BORDER);
		project.setLayoutData(gd);
		project.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});

		browse = createPushButton(projectSelection, "Browse...", null);
		browse.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});


		Group modelSelection =  new Group(root, SWT.BORDER);
		modelSelection.setText("Model project");
		modelSelection.setLayout(new GridLayout());
		GridData msgd = new GridData(GridData.FILL_BOTH);
		msgd.grabExcessHorizontalSpace = true;
		msgd.grabExcessVerticalSpace = true;
		modelSelection.setLayoutData(msgd);

		table = new Table(modelSelection, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		table.setLinesVisible (true);
		table.setHeaderVisible (true);
		table.setLayoutData(msgd);
		table.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				ProjectBinding p = (ProjectBinding)((Table)e.getSource()).getSelection()[0].getData();
				modelID = p.getProjectId();
				updateLaunchConfigurationDialog();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});

		String[] titles = {"name", "public", "ID"};
		for (String title : titles) {
			TableColumn column = new TableColumn (table, SWT.NONE);
			column.setText (title);
			column.setWidth(200);
		}

		Composite compositeCredential = new Composite(modelSelection, SWT.NULL);
		compositeCredential.setLayout(new GridLayout(5, false));

		final Label l1 = new Label(compositeCredential, SWT.RIGHT);
		l1.setText("Login: ");
		login = new Text(compositeCredential, SWT.BORDER | SWT.SINGLE);
		login.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});

		final Label l2 = new Label(compositeCredential, SWT.RIGHT);
		l2.setText("Password: ");

		password = new Text(compositeCredential, SWT.PASSWORD | SWT.BORDER | SWT.SINGLE);
		password.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});

		refresh = new Button(compositeCredential, SWT.PUSH);
		refresh.setText("Refresh");
		refresh.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ProgressMonitorDialog mon = new ProgressMonitorDialog(root.getShell());
				mon.open();
				populateProjectTable();
				mon.close();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

	}

	/**
	 * Populate the project table with user information.
	 */
	private void populateProjectTable() {
		GMMCredential credential = new GMMCredential(login.getText(), password.getText());
		for (TableItem item : table.getItems()) {
			item.dispose();
		}
		try {
			ProjectBinding [] projects = GMMAPIRestClient.getInstance().GETMyProjects(credential);
			for (ProjectBinding project : projects) {
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(new String[] {project.getName(), Boolean.toString(project.isPublic()), project.getProjectId()});
				item.setData(project);
			}
		} catch (OAuth2AccessDeniedException e) {
			IStatus err = new Status(
					Status.ERROR,
					Activator.PLUGIN_ID,
					Status.ERROR,
					"Login/password error\n\tPlease verify your information and be sure that you set a passord for your account.",
					e);
			StatusManager.getManager().handle(err, StatusManager.BLOCK);
		}

	}
	
	/**
	 * Handles the project browse button.
	 */
	private void handleBrowse() {
		FilteredItemsSelectionDialog dialog = new FilteredResourcesSelectionDialog(getShell(), false,
				ResourcesPlugin.getWorkspace().getRoot(), IResource.PROJECT);
		dialog.setTitle("Browse...");
		String path = project.getText();
		if (path != null && path.length() > 0 && new Path(path).lastSegment().length() > 0) {
			dialog.setInitialPattern(new Path(path).lastSegment());
		} else {
			dialog.setInitialPattern("**");
		}
		dialog.open();
		if (dialog.getResult() != null && dialog.getResult().length > 0
				&& dialog.getResult()[0] instanceof IProject) {
			project.setText(((IProject)dialog.getResult()[0]).getFullPath().toString());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			project.setText(configuration.getAttribute(GMMRunconfigConstant.CUSTOMGEN_PROJECT, ""));
		} catch (CoreException e) {
		}
		try {
			login.setText(configuration.getAttribute(GMMRunconfigConstant.LOGIN, ""));
		} catch (CoreException e) {
		}
		try {
			password.setText(configuration.getAttribute(GMMRunconfigConstant.PASSWORD, ""));
		} catch (CoreException e) {
		}
		
		try {
			modelID = configuration.getAttribute(GMMRunconfigConstant.MODEL_PROJECT, "");
		} catch (CoreException e) {
		}
		
		if (login.getText() != null && !login.getText().trim().isEmpty() && password.getText() != null && !password.getText().trim().isEmpty()) {
			populateProjectTable();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(GMMRunconfigConstant.CUSTOMGEN_PROJECT, project.getText());
		configuration.setAttribute(GMMRunconfigConstant.MODEL_PROJECT, modelID);
		configuration.setAttribute(GMMRunconfigConstant.LOGIN, login.getText());
		configuration.setAttribute(GMMRunconfigConstant.PASSWORD, password.getText());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return "&Project selection";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid(ILaunchConfiguration config) {
		boolean projectOK = project.getText() != null
				&& !project.getText().trim().isEmpty()
				&& ResourcesPlugin.getWorkspace().getRoot()
						.getProject(project.getText()).exists();
		boolean modelidOK = modelID != null && !modelID.trim().isEmpty();
		return projectOK && modelidOK;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return GID; 
	}

}
