package org.genmymodel.plugin.resource;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
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
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.statushandlers.StatusManager;
import org.genmymodel.common.api.GMMAPIRestClient;
import org.genmymodel.common.api.GMMCredential;
import org.genmymodel.common.api.ProjectBinding;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;

public class GMMExplorer extends ViewPart {
	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.genmymodel.plugin.resource.GMMExplorer";

	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action addAccount, deleteAccount, doubleClick;
	private ViewContentProvider content;

	class TreeObject implements IAdaptable {
		private String name;
		private TreeParent parent;
		private ProjectBinding project;

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

		@Override
		@SuppressWarnings("rawtypes")
		public Object getAdapter( Class adapter) {
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

	class ViewContentProvider implements IStructuredContentProvider,
			ITreeContentProvider {
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
				TreeParent parent = new TreeParent(((GMMCredential)users.get(i).get(0)).getUsername());
				for (int j = 1; j < users.get(i).size(); j++) {
					TreeObject child = new TreeObject(((ProjectBinding)users.get(i).get(j)).getName());
					child.setProject((ProjectBinding)users.get(i).get(j));
					parent.addChild(child);
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
				if (((GMMCredential)users.get(i).get(0)).getUsername().equals(element)) {
					return users.remove(users.get(i));
				}
			}
			return false;
		}
	}

	class ViewLabelProvider extends LabelProvider implements IColorProvider, IFontProvider {

		public String getText(Object obj) {
			return obj.toString();
		}

		@SuppressWarnings("deprecation")
		public Image getImage(Object obj) {
			String imageKey = ISharedImages.IMG_OBJ_PROJECT;
			if (obj instanceof TreeParent)
				imageKey = ISharedImages.IMG_OBJ_FOLDER;
			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		}

		public Font getFont(Object element) {
			if (element instanceof TreeParent)
				return getSite().getShell().getDisplay().getSystemFont();
			return null;
		}

		public Color getForeground(Object element) {
			if (element instanceof TreeParent)
				return getSite().getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK);
			return null;
		}

		public Color getBackground(Object element) {
			if (element instanceof TreeParent)
				return getSite().getShell().getDisplay().getSystemColor(SWT.COLOR_GRAY);
			return null;
		}
	}

	class NameSorter extends ViewerSorter {
	}

	class AddDialogAccount extends TitleAreaDialog {

		private Text UsernameText;
		private Text passwordText;

		private String Username;
		private String password;

		public AddDialogAccount(Shell parentShell) {
			super(parentShell);
		}

		@Override
		public void create() {
			super.create();
			setTitle("Add account");
			setMessage("Please insert your Username and password.", IMessageProvider.INFORMATION);
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
			Label usernameLabel = new Label(container, SWT.NONE);
			usernameLabel.setText("Username");

			GridData dataUsername = new GridData();
			dataUsername.grabExcessHorizontalSpace = true;
			dataUsername.horizontalAlignment = GridData.FILL;

			UsernameText = new Text(container, SWT.BORDER);
			UsernameText.setLayoutData(dataUsername);
		}

		private void createPassword(Composite container) {
			Label passwordLabel = new Label(container, SWT.NONE);
			passwordLabel.setText("Password");

			GridData dataPassword = new GridData();
			dataPassword.grabExcessHorizontalSpace = true;
			dataPassword.horizontalAlignment = GridData.FILL;
			passwordText = new Text(container, SWT.BORDER | SWT.PASSWORD);
			passwordText.setLayoutData(dataPassword);
		}

		@Override
		protected boolean isResizable() {
			return true;
		}

		private void saveInput() {
			Username = UsernameText.getText();
			password = passwordText.getText();
		}

		@Override
		protected void okPressed() {
			saveInput();
			super.okPressed();
		}

		public String getUsername() {
			return Username;
		}

		public String getPassword() {
			return password;
		}
	}

	/**
	 * The constructor.
	 */
	public GMMExplorer() {
		content = new ViewContentProvider();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(content);
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());

		makeActions();
		contributeToActionBars();
		doubleClickAction();
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(addAccount);
		manager.add(new Separator());
		manager.add(deleteAccount);
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(addAccount);
		manager.add(deleteAccount);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() {
		addAccount = new Action() {
			public void run() {
				AddDialogAccount dialogAccount = new AddDialogAccount(viewer.getControl().getShell());
				dialogAccount.open();
				addAccount(dialogAccount);
			}
		};
		addAccount.setText("Add");
		addAccount.setToolTipText("Add account");
		addAccount.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_DEF_VIEW));

		deleteAccount = new Action() {
			public void run() {
				removeAccount(((IStructuredSelection) viewer.getSelection())
						.getFirstElement().toString());
			}
		};
		deleteAccount.setText("Delete");
		deleteAccount.setToolTipText("Delete account");
		deleteAccount.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ETOOL_DELETE));

		doubleClick = new Action() {
			public void run() {				
				URIEditorInput input = new URIEditorInput(
						URI.createURI("genmymodel://"
								+ (((TreeObject)((IStructuredSelection) viewer.getSelection())
										.getFirstElement())).getProject().getProjectId()));
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
		};
	}

	private void addAccount(final AddDialogAccount dialogAccount) {
		GMMCredential credential = new GMMCredential(dialogAccount.getUsername(), dialogAccount.getPassword());
		try {
			ProjectBinding[] projects = GMMAPIRestClient.getInstance().GETMyProjects(credential);
			ArrayList<Object> list = new ArrayList<Object>();
			list.add(credential);
			for (ProjectBinding project : projects) {
				list.add(project);
			}
			content.addElement(list);
			content.initialize();
			viewer.setContentProvider(content);
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
			content.initialize();
			viewer.setContentProvider(content);
		}
	}

	private void doubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClick.run();
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}