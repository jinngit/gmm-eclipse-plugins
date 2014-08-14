package org.genmymodel.plugin.resource.explorer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IViewSite;
import org.genmymodel.common.account.GMMCredential;
import org.genmymodel.common.api.GMMAPIRestClient;
import org.genmymodel.common.api.ProjectBinding;

/**
 * 
 * @author Ali Gourch
 */
public class ViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {
	protected TreeParent invisibleRoot;
	protected List<List<Object>> users = new ArrayList<List<Object>>();
	protected GMMAPIRestClient client;
	protected IViewSite site;

	public ViewContentProvider(GMMAPIRestClient client, IViewSite site) {
		this.client = client;
		this.site = site;
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}

	public Object[] getElements(Object parent) {
		if (parent.equals(site)) {
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

	protected void initialize() {
		TreeParent root = new TreeParent("Users");
		for (int i = 0; i < users.size(); i++) {
			TreeParent parent = new TreeParent(((GMMCredential) users.get(i)
					.get(0)).getUsername());
			TreeParent publicChild = new TreeParent("public");
			TreeParent privateChild = new TreeParent("private");
			TreeParent sharedChild = new TreeParent("shared");
			parent.addChild(publicChild);
			parent.addChild(privateChild);
			parent.addChild(sharedChild);
			parent.setCredential((GMMCredential) users.get(i).get(0));
			ProjectBinding[] sharedProjects = client
					.GETSharedProjects((GMMCredential) users.get(i).get(0));
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
				} else {
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
