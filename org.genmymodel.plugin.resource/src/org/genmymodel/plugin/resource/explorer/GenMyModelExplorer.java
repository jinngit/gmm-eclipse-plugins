package org.genmymodel.plugin.resource.explorer;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.lingala.zip4j.core.ZipFile;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.statushandlers.StatusManager;
import org.genmymodel.common.account.GMMCredential;
import org.genmymodel.common.account.GMMKeyStore;
import org.genmymodel.common.api.CustomGeneratorBinding;
import org.genmymodel.common.api.GMMAPIRestClient;
import org.genmymodel.common.api.GMMAPIRestClient.CompilCallResult;
import org.genmymodel.common.api.ProjectBinding;
import org.genmymodel.plugin.resource.Activator;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;

/**
 * This class manage the explorer views.
 * 
 * @author Ali Gourch
 * @author Vincent Aranega
 */
public class GenMyModelExplorer extends ViewPart {
	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.genmymodel.plugin.resource.explorer.GenMyModelExplorer";

	private GMMAPIRestClient client;
	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action addAccount, deleteAccount, refreshAccount, openProject, generateProject, deleteProject;
	private ViewContentProvider content;
	private IMemento save;
	private GMMKeyStore keyStore;
	
	/**
	 * The constructor.
	 */
	public GenMyModelExplorer() {
		client = GMMAPIRestClient.getInstance();
		keyStore = GMMKeyStore.getInstance();
	}

	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		this.save = memento == null ? XMLMemento.createWriteRoot("view")
				: memento;

		for (IMemento child : save.getChildren("credential")) {
			keyStore.addCredential(
					child.getString("username"),
					new GMMCredential(child.getString("username"), child
							.getString("password")));
			keyStore.loadCredential(child.getString("username"));
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		content = new ViewContentProvider(client, getViewSite());
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(content);
		viewer.setLabelProvider(new ViewLabelProvider(getSite()));
		viewer.setInput(getViewSite());
		
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[]{FileTransfer.getInstance()};
	    viewer.addDragSupport(operations, transferTypes, new DragListener(viewer));
	    viewer.addDropSupport(operations, transferTypes, new DropListener(parent, viewer));
	    
		if (save != null) {
			IMemento[] credentials = save.getChildren("credential");
			save = XMLMemento.createWriteRoot("view");
			for (IMemento credential : credentials) {
				addAccount(new GMMCredential(credential.getString("username"),
						credential.getString("password")));
			}
		}

		makeActions(parent);
		contributeToActionBars();
		doubleClickProjectAction();
		rightClickProjectAction();
		pageProjectAction();
	}
	
	private void pageProjectAction() {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		// adding a listener
		IPartListener2 pl = new IPartListener2() {
			public void partClosed(final IWorkbenchPartReference partRef) {
				BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
					public void run() {
						ProgressMonitorDialog dialog = new ProgressMonitorDialog(
								PlatformUI.getWorkbench()
										.getActiveWorkbenchWindow().getShell());
						try {
							dialog.run(true, false,
									new IRunnableWithProgress() {
										public void run(IProgressMonitor monitor) {
											// monitor.beginTask(null,
											// IProgressMonitor.UNKNOWN);
											Display.getDefault().asyncExec(
													new Runnable() {
														public void run() {
															// TODO close the active editor.
//															PlatformUI
//																	.getWorkbench()
//																	.getActiveWorkbenchWindow()
//																	.getActivePage()
//																	.getActiveEditor()
//																	.;
														}
													});
											// monitor.done();
					}
							});
						} catch (InvocationTargetException | InterruptedException e) {
							e.printStackTrace();
						}
					}
				});
			}

			@Override
			public void partActivated(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub

			}

			@Override
			public void partBroughtToTop(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub

			}

			@Override
			public void partDeactivated(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub

			}

			@Override
			public void partOpened(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub

			}

			@Override
			public void partHidden(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub

			}

			@Override
			public void partVisible(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub

			}

			@Override
			public void partInputChanged(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub

			}

		};
		page.addPartListener(pl);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(refreshAccount);
		manager.add(new Separator());
		manager.add(addAccount);
		manager.add(new Separator());
		manager.add(deleteAccount);
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(refreshAccount);
		manager.add(addAccount);
		manager.add(deleteAccount);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions(final Composite parent) {
		ImageDescriptor image;
		refreshAccount = new Action() {
			public void run() {
				refreshAccounts();
			}
		};
		refreshAccount.setText("Refresh");
		refreshAccount.setToolTipText("Refresh accounts");
		image = createImageDescriptor("icons/refresh.gif");
		refreshAccount.setImageDescriptor(image);

		addAccount = new Action() {
			public void run() {
				AddAccountDialog dialog = new AddAccountDialog(viewer
						.getControl().getShell());
				dialog.open();
				addAccount(new GMMCredential(dialog.getUsername(),
						dialog.getPassword()));
			}
		};
		addAccount.setText("Add");
		addAccount.setToolTipText("Add account");
		image = createImageDescriptor("icons/add_obj.gif");
		addAccount.setImageDescriptor(image);

		deleteAccount = new Action() {
			public void run() {
				if (((TreeObject) ((IStructuredSelection) viewer.getSelection())
						.getFirstElement()) != null) {
					if (MessageDialog.openConfirm(parent.getShell(), "Removing account", "Are you sure you want to remove this account from the view ?")) {
						removeAccount(((IStructuredSelection) viewer.getSelection()).getFirstElement().toString());
					}
				}
			}
		};
		deleteAccount.setText("Delete");
		deleteAccount.setToolTipText("Delete account");
		image = createImageDescriptor("icons/delete_obj.gif");
		deleteAccount.setImageDescriptor(image);

		openProject = new Action() {
			public void run() {
				if (((TreeObject) ((IStructuredSelection) viewer.getSelection())
						.getFirstElement()).getProject() != null) {
					final URIEditorInput input = new URIEditorInput(
							URI.createURI("genmymodel://"
									+ (((TreeObject) ((IStructuredSelection) viewer
											.getSelection()).getFirstElement()))
											.getProject().getProjectId()));
					keyStore.loadCredential((((TreeObject) ((IStructuredSelection) viewer
							.getSelection()).getFirstElement()))
							.getCredential().getUsername());
					try {
						PlatformUI
								.getWorkbench()
								.getActiveWorkbenchWindow()
								.getActivePage()
								.openEditor(
										input,
										"org.eclipse.emf.ecore.presentation.EcoreEditorID",
										true);
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			}
		};

		generateProject = new Action() {
			public void run() {
				final GenerationDialog dialog = new GenerationDialog(viewer, client);
				dialog.open();
				if (dialog.getReturnCode() == Window.OK) {
					generateProject(dialog.getGenerator(), dialog.getDestination());
				}
			}
		};
		generateProject.setText("Generate");

		deleteProject = new Action() {
			public void run() {
				if (MessageDialog.openConfirm(parent.getShell(), "Deleting project", "Are you sure you want to delete this project ?")) {
					deleteProject();
				}
			}
		};
		deleteProject.setText("Delete");		
	}
	private void openProject(final Action action) {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow()
								.getShell());
				try {
					dialog.run(true, false, new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor) {
//							monitor.beginTask(null, IProgressMonitor.UNKNOWN);
//							try {
//								Thread.sleep(1000);
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									action.run();
								}
							});
//							monitor.done();
						}
					});
				} catch (InvocationTargetException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	protected void refreshAccounts() {
		Map<String, GMMCredential> tmp = new HashMap<String, GMMCredential>(
				keyStore.getCredentials());
		for (GMMCredential credential : tmp.values()) {
			removeAccount(credential.getUsername());
			addAccount(new GMMCredential(credential.getUsername(),
					credential.getPassword()));
		}				
	}

	private ImageDescriptor createImageDescriptor(String filename) {
		Bundle bundle = FrameworkUtil.getBundle(this.getClass());
		URL url = FileLocator.find(bundle, new Path(filename), null);
		ImageDescriptor image = ImageDescriptor.createFromURL(url);
		return image;
	}

	private void addAccount(GMMCredential credential) {
		try {
			ProjectBinding[] projects = client.GETMyProjects(credential);
			ArrayList<Object> list = new ArrayList<Object>();
			list.add(credential);
			for (ProjectBinding project : projects) {
				list.add(project);
			}

			if (save != null) {
				IMemento child = save.createChild("credential");
				child.putString("username", credential.getUsername());
				child.putString("password", credential.getPassword());
			}

			content.addElement(list);
			content.initialize();
			viewer.setContentProvider(content);
			keyStore.addCredential(credential.getUsername(), credential);
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

	private void removeAccount(String account) {
		if (content.removeElement(account)) {
			if (save != null) {
				IMemento[] credentials = save.getChildren("credential");
				save = XMLMemento.createWriteRoot("view");
				for (IMemento credential : credentials) {
					if (!credential.getString("username").equalsIgnoreCase(
							account)) {
						IMemento child = save.createChild("credential");
						child.putString("username",
								credential.getString("username"));
						child.putString("password",
								credential.getString("password"));
					}
				}
			}
			content.initialize();
			viewer.setContentProvider(content);
			keyStore.removeCredential(account);
		}
	}
	
	private void deleteProject() {
		GMMCredential credential = ((TreeObject) ((IStructuredSelection) viewer
				.getSelection()).getFirstElement()).getCredential();
		String projectID = ((TreeObject) ((IStructuredSelection) viewer
				.getSelection()).getFirstElement()).getProject().getProjectId();
		client.DELETEProject(credential, projectID);
		TreeObject item = (TreeObject) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
		item.getParent().removeChild(item);
		viewer.refresh();
	}

	private void generateProject(final CustomGeneratorBinding generator, final IContainer destination) {
		final GMMCredential credential = ((TreeObject) ((IStructuredSelection) viewer
				.getSelection()).getFirstElement()).getCredential();
		final ProjectBinding project = ((TreeObject) ((IStructuredSelection) viewer
				.getSelection()).getFirstElement()).getProject();
		if(generator != null && destination != null) {			
				Job job = new Job("Generation of " + project.getName() + " using " + generator.getName() + "...") {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						try {
							CompilCallResult result = GMMAPIRestClient.getInstance().POSTCustomgenLaunch(project.getProjectId(), generator.getGeneratorId(), credential);
							if (!result.callResult.hasErrors()) {
								new ZipFile(result.zip).extractAll(destination.getLocation().toFile().getAbsolutePath());
								destination.refreshLocal(IResource.DEPTH_ONE, monitor);
								FileUtils.forceDelete(result.zip);
							} else {
								MultiStatus err = new MultiStatus(Activator.PLUGIN_ID, Status.ERROR,
										"Error during code generation", null);

								for (Entry<String, List<String>> entry : result.callResult.getErrors().entrySet()) {
									MultiStatus lab = new MultiStatus(Activator.PLUGIN_ID,
											Status.ERROR, entry.getKey(), null);
									for (String s : entry.getValue()) {
										lab.add(new Status(Status.ERROR, Activator.PLUGIN_ID,
												Status.ERROR, s, null));
									}
									err.add(lab);
								}

								StatusManager.getManager().handle(err, StatusManager.SHOW);
								return Status.CANCEL_STATUS;
							}
						} catch (Exception e) {
							e.printStackTrace();
							
							return Status.CANCEL_STATUS;
						}
						return Status.OK_STATUS;
					}
				};
				job.schedule();			
		}
	}
	
	private void doubleClickProjectAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				openProject(openProject);
			}
		});
	}

	private void rightClickProjectAction() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				GenMyModelExplorer.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
	}

	private void fillContextMenu(IMenuManager manager) {
		drillDownAdapter.addNavigationActions(manager);
		manager.add(new Separator());
		if (((TreeObject) ((IStructuredSelection) viewer.getSelection())
				.getFirstElement()).getProject() != null) {
			manager.add(generateProject);
			manager.add(deleteProject);
		}
	}

	public void saveState(IMemento memento) {
		super.saveState(memento);
		memento.putMemento(save);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}