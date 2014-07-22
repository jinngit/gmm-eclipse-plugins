package org.genmymodel.engine.connector.handlers;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.genmymodel.engine.connector.project.IGenMyModelProject;

/**
 * This class provides some access primitive that helps handling projects,
 * IResource. It also provides facilities for GenMyModel API communication.
 * 
 * @author Vincent Aranega
 *
 */
public abstract class GMMAbstractHandler extends AbstractHandler {

	protected ExecutionEvent currentEvent;
	public static File systemTmpFolder;
	
	static {
		systemTmpFolder = new File(System.getProperty("java.io.tmpdir"));
	}

	/**
	 * Gets the stored current event.
	 * 
	 * @return The stored ExecutionEvent.
	 */
	public ExecutionEvent getCurrentEvent() {
		return currentEvent;
	}

	/**
	 * Store the current event.
	 * 
	 * @param event
	 *            The current ExecutionEvent.
	 */
	public void setCurrentEvent(ExecutionEvent event) {
		this.currentEvent = event;
	}

	/**
	 * Store the current event when the execute method is called.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		setCurrentEvent(event);
		return null;
	}

	/**
	 * Extracts the selected IResource from the stored current ExecutionEvent.
	 * 
	 * @return The selected IResource.
	 */
	protected IResource extractSelection() {
		ISelection sel = HandlerUtil.getCurrentSelection(getCurrentEvent());
		if (!(sel instanceof IStructuredSelection)) {
			return null;
		}
		IStructuredSelection ss = (IStructuredSelection) sel;
		Object element = ss.getFirstElement();
		if (element instanceof IResource) {
			return (IResource) element;
		}
		if (!(element instanceof IAdaptable)) {
			return null;
		}
		IAdaptable adaptable = (IAdaptable) element;
		Object adapter = adaptable.getAdapter(IResource.class);
		return (IResource) adapter;
	}

	/**
	 * Extracts the selected IProject from the stored current ExecutionEvent.
	 * 
	 * @return The selected IProject.
	 */
	protected IProject extractProject() {
		return extractSelection().getProject();
	}
	
	protected IGenMyModelProject getGMMProject() {
		return new IGenMyModelProject(extractProject());
	}

}
