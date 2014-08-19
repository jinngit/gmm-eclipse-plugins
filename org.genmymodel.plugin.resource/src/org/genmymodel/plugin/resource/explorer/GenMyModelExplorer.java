package org.genmymodel.plugin.resource.explorer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
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
					if (MessageDialog.openConfirm(parent.getShell(), "Deleting account", "Are you sure you want to delete this account ?")) {
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
					URIEditorInput input = new URIEditorInput(
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
				GenerationDialog dialog = new GenerationDialog(viewer, client);
				dialog.open();
				generateProject(dialog.getGenerator(), dialog.getDestination());
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

	private void generateProject(CustomGeneratorBinding generator, String destination) {
		GMMCredential credential = ((TreeObject) ((IStructuredSelection) viewer
				.getSelection()).getFirstElement()).getCredential();
		ProjectBinding project = ((TreeObject) ((IStructuredSelection) viewer
				.getSelection()).getFirstElement()).getProject();
		if(generator != null && destination != null) {
			try {
				// Copy from github
				File customgen = File.createTempFile("customgen", ".zip");
				String path = customgen.getParent();
				String url = generator
						.getGeneratorURL()
						.replace(".git", "")
						.concat("/archive/")
						.concat((generator.getGeneratorBranch() != "" ? generator
								.getGeneratorBranch() : "master") + ".zip");
				FileUtils.copyURLToFile(new URL(url), customgen, 10000, 10000);
	
				// Unzip github archive
				new ZipFile(customgen).extractAll(path + "/customgen");
				FileUtils.forceDelete(customgen);
	
				ZipParameters parameters = new ZipParameters();
				parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
				parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
	
				// Compile
				CompilCallResult result = client
						.POSTCompile(customgen(new File(path + "/customgen"),
								new ZipFile(path + "/customgen.zip"), parameters)
								.getFile());
				new ZipFile(result.zip).extractAll(path + "/customgen");
				FileUtils.forceDelete(result.zip);
				new ZipFile(path + "/customgen.zip")
						.extractAll(path + "/customgen");
				FileUtils.forceDelete(new File(path + "/customgen.zip"));
	
				ZipFile compile = new ZipFile(path + "/customgen.zip");
				compile.addFolder(path + "/customgen", parameters);
				FileUtils.forceDelete(new File(path + "/customgen"));
	
				// Execute
				result = client.POSTExec(compile.getFile(), project.getProjectId(),
						credential);
				FileUtils.forceDelete(compile.getFile());
				FileUtils.copyFileToDirectory(result.zip, new File(destination));
				FileUtils.forceDelete(result.zip);
			} catch (IOException | ZipException e) {
				e.printStackTrace();
			}
		}
	}

	private ZipFile customgen(File tmp, ZipFile zipFile,
			ZipParameters parameters) {
		try {
			for (File file : tmp.listFiles()) {
				if (file.getName().equalsIgnoreCase("codegen")
						|| file.getName().equalsIgnoreCase("metamodels")
						|| file.getName().equalsIgnoreCase("transformations")
						|| file.getName().equalsIgnoreCase("generator.xml")) {
					if (file.isDirectory()) {
						zipFile.addFolder(file.getAbsoluteFile(), parameters);
					} else if (file.isFile()) {
						zipFile.addFile(file.getAbsoluteFile(), parameters);
					}
				}
				if (file.isDirectory()) {
					customgen(file, zipFile, parameters);
				}
			}
			FileUtils.deleteDirectory(tmp);
		} catch (ZipException | IOException e) {
			e.printStackTrace();
		}
		return zipFile;
	}

	private void doubleClickProjectAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				openProject.run();
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