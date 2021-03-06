package org.genmymodel.plugin.resource.explorer;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Composite;
import org.genmymodel.common.api.GMMAPIRestClient;
import org.genmymodel.common.api.ProjectPostBinding;
import org.genmymodel.plugin.resource.Activator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;

/**
 * @author Ali Gourch
 * @author Vincent Aranega
 *
 */
public class DropListener extends ViewerDropAdapter {
	private Composite parent;
	private TreeParent target;
	private TreeViewer viewer;

	public DropListener(Composite parent, TreeViewer viewer) {
		super(viewer);
		this.parent = parent;
		this.viewer = viewer;
	}

	@Override
	public void drop(DropTargetEvent event) {
		target = (TreeParent) determineTarget(event);
		super.drop(event);
	}

	// This method performs the actual drop
	@Override
	public boolean performDrop(Object data) {
		if (data instanceof String[]) {
			String[] s = (String[]) data;
			if (s == null || s.length == 0) {
				return true;
			}
			File file = new File(s[0]);
			try {
				InputDialog input = new InputDialog(parent.getShell(), "Project name", "Please enter the name of your project:", "Project name", new Validator());
				input.open();
				ProjectPostBinding project = new ProjectPostBinding();
				project.setName(input.getValue());
				project.setPublic(target.getName().equalsIgnoreCase("public"));
				project.setData(FileUtils.readFileToByteArray(file));

				ResponseEntity<ProjectPostBinding> response = GMMAPIRestClient.getInstance().POSTImportedProject(target.getParent().getCredential(), project);
				TreeObject child = new TreeObject(project.getName());
				child.setCredential(target.getParent().getCredential());
				project.setProjectId(response.getBody().getProjectId());
				child.setProject(project);
				target.addChild(child);
				viewer.refresh();
			} catch (HttpServerErrorException e) {
				IStatus err = new Status(Status.ERROR, Activator.PLUGIN_ID, Status.ERROR, "Please check if it a UML model and it has XMI format.", e);
				ErrorDialog.openError(parent.getShell(), "Error while importing your model into GenMyModel", "Error while importing your model into GenMyModel", err);
				return false;
			} catch (IOException e) {
				IStatus err = new Status(Status.ERROR, Activator.PLUGIN_ID, Status.ERROR, "Error while reading your model.", e);
				ErrorDialog.openError(parent.getShell(), "Error while importing your model into GenMyModel", "Error while importing your model into GenMyModel", err);
				return false;
			}
		}		
		return true;
	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {
		if (FileTransfer.getInstance().isSupportedType(transferType) && target != null) {
			if(target.toString().equalsIgnoreCase("public") || target.toString().equalsIgnoreCase("private")) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This class validates a String.
	 */
	class Validator implements IInputValidator {
	  /**
	   * Validates the String. Returns null for no error, or an error message
	   * 
	   * @param newText the String to validate
	   * @return String
	   */
	  public String isValid(String str) {
	    int len = str.length();

	    // Determine if input is too short or too long
	    if (len < 4) return "Too short";
	    if (len > 50) return "Too long";
	    return null;
	  }
	}

}