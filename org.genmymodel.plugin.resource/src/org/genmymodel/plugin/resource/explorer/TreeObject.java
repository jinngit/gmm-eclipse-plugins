package org.genmymodel.plugin.resource.explorer;

import org.eclipse.core.runtime.IAdaptable;
import org.genmymodel.common.account.GMMCredential;
import org.genmymodel.common.api.ProjectBinding;

/**
 * 
 * @author Ali Gourch
 */
public class TreeObject implements IAdaptable {
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
