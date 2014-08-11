package org.genmymodel.plugin.resource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IAdaptable;
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
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
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
	public static final String ID = "org.genmymodel.plugin.resource.GMMExplorer";

	private GMMAPIRestClient client;
	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action addAccount, deleteAccount, refreshAccount, doubleClick, rightClick;
	private ViewContentProvider content;
	private IMemento save;
	private GMMKeyStore keyStore;

	/**
	 * The constructor.
	 */
	public GenMyModelExplorer() {
		client = GMMAPIRestClient.getInstance();
		content = new ViewContentProvider();
		keyStore = GMMKeyStore.getInstance();
	}

	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		this.save = memento == null ? XMLMemento.createWriteRoot("view") : memento;

		for (IMemento child : save.getChildren("credential")) {
			keyStore.addCredential(
					child.getString("username"),
					new GMMCredential(
							child.getString("username"),
							child.getString("password")));
			keyStore.loadCredential(child.getString("username"));
		}
	}

	class TreeObject implements IAdaptable {
		private String name;
		private TreeParent parent;
		private ProjectBinding project;
		private GMMCredential credential;

		public TreeObject(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setParent(TreeParent parent) {
			this.parent = parent;
		}

		public TreeParent getParent() {
			return parent;
		}

		public String toString() {
			return getName();
		}

		public ProjectBinding getProject() {
			return project;
		}

		public void setProject(ProjectBinding project) {
			this.project = project;
		}

		public GMMCredential getCredential() {
			return credential;
		}

		public void setCredential(GMMCredential credential) {
			this.credential = credential;
		}

		@Override
		@SuppressWarnings("rawtypes")
		public Object getAdapter(Class adapter) {
			return null;
		}
	}

	class TreeParent extends TreeObject {
		private ArrayList<TreeObject> children;

		public TreeParent(String name) {
			super(name);
			children = new ArrayList<TreeObject>();
		}

		public void addChild(TreeObject child) {
			children.add(child);
			child.setParent(this);
		}

		public void removeChild(TreeObject child) {
			children.remove(child);
			child.setParent(null);
		}

		public TreeObject[] getChildren() {
			return (TreeObject[]) children.toArray(new TreeObject[children.size()]);
		}

		public boolean hasChildren() {
			return children.size() > 0;
		}
	}

	class ViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {
		private TreeParent invisibleRoot;
		List<List<Object>> users = new ArrayList<List<Object>>();

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			if (parent.equals(getViewSite())) {
				if (invisibleRoot == null)
					initialize();
				return getChildren(invisibleRoot);
			}
			return getChildren(parent);
		}

		public Object getParent(Object child) {
			if (child instanceof TreeObject) {
				return ((TreeObject) child).getParent();
			}
			return null;
		}

		public Object[] getChildren(Object parent) {
			if (parent instanceof TreeParent) {
				return ((TreeParent) parent).getChildren();
			}
			return new Object[0];
		}

		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeParent)
				return ((TreeParent) parent).hasChildren();
			return false;
		}

		private void initialize() {
			TreeParent root = new TreeParent("Users");
			for (int i = 0; i < users.size(); i++) {
				TreeParent parent = new TreeParent(((GMMCredential) users
						.get(i).get(0)).getUsername());
				TreeParent publicChild = new TreeParent("public");
				TreeParent privateChild = new TreeParent("private");
				TreeParent sharedChild = new TreeParent("shared");
				parent.addChild(publicChild);
				parent.addChild(privateChild);
				parent.addChild(sharedChild);
				parent.setCredential((GMMCredential) users.get(i).get(0));
				ProjectBinding[] sharedProjects = client.GETSharedProjects((GMMCredential) users.get(i).get(0));
				for (ProjectBinding project : sharedProjects) {
					TreeObject child = new TreeObject(project.getName());
					child.setCredential((GMMCredential) users.get(i).get(0));
					child.setProject(project);
					sharedChild.addChild(child);
				}
				for (int j = 1; j < users.get(i).size(); j++) {
					TreeObject child = new TreeObject(((ProjectBinding) users
							.get(i).get(j)).getName());
					child.setCredential((GMMCredential) users.get(i).get(0));
					child.setProject((ProjectBinding) users.get(i).get(j));
					if (((ProjectBinding) users.get(i).get(j)).isPublic()) {
						publicChild.addChild(child);
					}
					else {
						privateChild.addChild(child);
					}
				}
				root.addChild(parent);
			}

			invisibleRoot = new TreeParent("");
			invisibleRoot.addChild(root);
		}

		public void addElement(ArrayList<Object> element) {
			users.add(element);
		}

		public boolean removeElement(String element) {
			for (int i = 0; i < users.size(); i++) {
				if (((GMMCredential) users.get(i).get(0)).getUsername().equals(
						element)) {
					return users.remove(users.get(i));
				}
			}
			return false;
		}
	}

	class ViewLabelProvider extends LabelProvider implements IColorProvider,
			IFontProvider {
		public String getText(Object obj) {
			return obj.toString();
		}

		@SuppressWarnings("deprecation")
		public Image getImage(Object obj) {
			String imageKey = ISharedImages.IMG_OBJ_PROJECT;
			if (obj instanceof TreeParent) {
				imageKey = ISharedImages.IMG_OBJ_FOLDER;
			}
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(imageKey);
		}

		public Font getFont(Object element) {
			if (element instanceof TreeParent)
				return getSite().getShell().getDisplay().getSystemFont();
			return null;
		}

		public Color getForeground(Object element) {
			if (element instanceof TreeParent)
				return getSite().getShell().getDisplay()
						.getSystemColor(SWT.COLOR_BLACK);
			return null;
		}

		public Color getBackground(Object element) {
			if (element instanceof TreeParent) {
				return getSite().getShell().getDisplay()
						.getSystemColor(SWT.COLOR_GRAY);
			}
			return null;
		}
	}

	class AddAccountDialog extends TitleAreaDialog {
		private Text usernameText;
		private Text passwordText;

		private String username;
		private String password;

		public AddAccountDialog(Shell parentShell) {
			super(parentShell);
		}

		@Override
		public void create() {
			super.create();
			setTitle("Add account");
			setMessage("Please insert your username and password.", IMessageProvider.INFORMATION);
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite area = (Composite) super.createDialogArea(parent);
			Composite container = new Composite(area, SWT.NONE);
			container.setLayoutData(new GridData(GridData.FILL_BOTH));
			GridLayout layout = new GridLayout(2, false);
			container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			container.setLayout(layout);

			createUsername(container);
			createPassword(container);

			return area;
		}

		private void createUsername(Composite container) {
			Label label = new Label(container, SWT.NONE);
			label.setText("Username");

			GridData data = new GridData();
			data.grabExcessHorizontalSpace = true;
			data.horizontalAlignment = GridData.FILL;

			usernameText = new Text(container, SWT.BORDER);
			usernameText.setLayoutData(data);
		}

		private void createPassword(Composite container) {
			Label label = new Label(container, SWT.NONE);
			label.setText("Password");

			GridData data = new GridData();
			data.grabExcessHorizontalSpace = true;
			data.horizontalAlignment = GridData.FILL;
			passwordText = new Text(container, SWT.BORDER | SWT.PASSWORD);
			passwordText.setLayoutData(data);
		}

		@Override
		protected boolean isResizable() {
			return true;
		}

		private void saveInput() {
			username = usernameText.getText();
			password = passwordText.getText();
		}

		@Override
		protected void okPressed() {
			saveInput();
			super.okPressed();
		}

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}
	}
	
	class GenerationDialog extends TitleAreaDialog {
		private Combo generatorCombo;
		private CustomGeneratorBinding generator;
		private DirectoryDialog destinationDirectory;
		private Text destinationText;
		private Button destiontionButton;
		private String destination;

		public GenerationDialog(Shell parentShell) {
			super(parentShell);
		}

		@Override
		public void create() {
			super.create();
			setTitle("Generation");
			setMessage("Please choose your generator and the destination.", IMessageProvider.INFORMATION);
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite area = (Composite) super.createDialogArea(parent);
			Composite container = new Composite(area, SWT.NONE);
			container.setLayoutData(new GridData(GridData.FILL_BOTH));
			GridLayout layout = new GridLayout(3, false);
			container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			container.setLayout(layout);

			createGenerators(container);
			createDestination(container);

			return area;
		}

		private void createGenerators(Composite container) {
			Label label = new Label(container, SWT.NONE);
			label.setText("Generator");

			GridData data = new GridData();
			data.grabExcessHorizontalSpace = true;
			data.horizontalAlignment = GridData.FILL;

			generatorCombo = new Combo(container, SWT.BORDER | SWT.READ_ONLY);			
			try {
				GMMCredential credential = ((TreeObject) ((IStructuredSelection) viewer.getSelection()).getFirstElement()).getCredential();
				CustomGeneratorBinding[] customGenerators = client.GETMyCustomGenerators(credential);
				for (CustomGeneratorBinding customGenerator : customGenerators) {
					generatorCombo.add(customGenerator.getName());
					generatorCombo.setData(customGenerator.getName(), customGenerator);
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
			generatorCombo.select(0);
			generatorCombo.setLayoutData(data);
			new Label(container, SWT.NONE);
		}
		
		private void createDestination(final Composite container) {
			Label label = new Label(container, SWT.NONE);
			label.setText("Destination");
			label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));			
			destinationText = new Text(container, SWT.BORDER | SWT.READ_ONLY);
			destinationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			destiontionButton = new Button(container, SWT.BORDER);
			destiontionButton.setText("Browse...");
			destiontionButton.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					destinationDirectory = new DirectoryDialog(container.getShell(), SWT.OPEN | SWT.BORDER | SWT.READ_ONLY);
					String dir = destinationDirectory.open();
					if (dir != null) {
						destinationText.setText(dir);
					}
				}				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {}
			});
			destiontionButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		}

		@Override
		protected boolean isResizable() {
			return true;
		}

		private void saveInput() {
			generator = (CustomGeneratorBinding) generatorCombo.getData(generatorCombo.getText());
			destination = destinationText.getText();
		}

		@Override
		protected void okPressed() {
			saveInput();
			super.okPressed();
		}

		public CustomGeneratorBinding getGenerator() {
			return generator;
		}

		public String getDestination() {
			return destination;
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(content);
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setInput(getViewSite());

		if (save != null) {
			IMemento[] credentials = save.getChildren("credential");
			save = XMLMemento.createWriteRoot("view");
			for (IMemento credential : credentials) {
				addAccount(new GMMCredential(credential.getString("username"),
						credential.getString("password")));
			}
		}

		makeActions();
		contributeToActionBars();
		doubleClickAction();
		rightClickAction();
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

	private void makeActions() {
		ImageDescriptor image;
		refreshAccount = new Action() {
			public void run() {
				Map<String, GMMCredential> tmp = new HashMap<String,GMMCredential>(keyStore.getCredentials());
				for (GMMCredential credential : tmp.values()) {
					removeAccount(credential.getUsername());
					addAccount(new GMMCredential(credential.getUsername(), credential.getPassword()));
				}
			}
		};
		refreshAccount.setText("Refresh");
		refreshAccount.setToolTipText("Refresh accounts");
		image = createImageDescriptor("icons/refresh.gif");		
		refreshAccount.setImageDescriptor(image);
		
		addAccount = new Action() {
			public void run() {
				AddAccountDialog dialog = new AddAccountDialog(viewer.getControl().getShell());
				dialog.open();
				addAccount(new GMMCredential(dialog.getUsername(), dialog.getPassword()));
			}
		};
		addAccount.setText("Add");
		addAccount.setToolTipText("Add account");
		image = createImageDescriptor("icons/add_obj.gif");
		addAccount.setImageDescriptor(image);

		deleteAccount = new Action() {
			public void run() {
				if (((TreeObject) ((IStructuredSelection) viewer.getSelection()).getFirstElement()) != null) {
					removeAccount(((IStructuredSelection) viewer.getSelection()).getFirstElement().toString());
				}
			}
		};
		deleteAccount.setText("Delete");
		deleteAccount.setToolTipText("Delete account");
		image = createImageDescriptor("icons/delete_obj.gif");
		deleteAccount.setImageDescriptor(image);

		doubleClick = new Action() {
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

		rightClick = new Action() {
			public void run() {
				GenerationDialog dialog = new GenerationDialog(viewer.getControl().getShell());
				dialog.open();
				generation(dialog.getGenerator(), dialog.getDestination());
				
			}
		};
		rightClick.setText("Generate");
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
	
	private void generation(CustomGeneratorBinding generator, String destination) {
		ProjectBinding project = ((TreeObject) ((IStructuredSelection) viewer.getSelection()).getFirstElement()).getProject();
		GMMCredential credential = ((TreeObject) ((IStructuredSelection) viewer.getSelection()).getFirstElement()).getCredential();

		System.out.println(generator.getGeneratorURL());
		try {
			//Copy from github 
			File customgen = File.createTempFile("customgen", ".zip");
			String path = customgen.getParent();
			String url = generator.getGeneratorURL().replace(".git", "").concat("/archive/master.zip");
			FileUtils.copyURLToFile(new URL(url), customgen, 10000, 10000);
			
			//Unzip github archive
			new ZipFile(customgen).extractAll(path+"/customgen");
			FileUtils.forceDelete(customgen);
			
			ZipParameters parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
			
			//Compile
			CompilCallResult result = client.POSTCompile(customgen(new File(path+"/customgen"), new ZipFile(path+"/customgen.zip"), parameters).getFile());
			new ZipFile(result.zip).extractAll(path+"/customgen");
			FileUtils.forceDelete(result.zip);
			new ZipFile(path+"/customgen.zip").extractAll(path+"/customgen");
			FileUtils.forceDelete(new File(path+"/customgen.zip"));
			
			ZipFile compile = new ZipFile(path+"/customgen.zip");
			compile.addFolder(path+"/customgen", parameters);
			FileUtils.forceDelete(new File(path+"/customgen"));
			
			//Execute
			result = client.POSTExec(compile.getFile(), project.getProjectId(), credential);
			FileUtils.forceDelete(compile.getFile());
			FileUtils.copyFileToDirectory(result.zip, new File(destination));
			FileUtils.forceDelete(result.zip);
		} catch (IOException | ZipException e) {
			e.printStackTrace();
		}
	}
	
	private ZipFile customgen(File tmp, ZipFile zipFile, ZipParameters parameters) {
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

	private void doubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClick.run();
			}
		});
	}
	
	private void rightClickAction() {
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
			manager.add(rightClick);
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