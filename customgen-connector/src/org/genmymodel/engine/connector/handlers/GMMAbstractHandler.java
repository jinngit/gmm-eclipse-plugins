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
import org.genmymodel.engine.connector.project.GenMyModelProject;

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
	 * Store the current event when the execute method is called. Each sub-class
	 * that extends this class <b>MUST</b> call super.execute(event) before it
	 * can access the GenMyModel project.
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

	/**
	 * Returns the GenMyModel project handled by this handler. The GenMyModel
	 * project is set each time the "execute" method is call.
	 * 
	 * @return The current GenMyModel project.
	 */
	protected GenMyModelProject getGMMProject() {
		return new GenMyModelProject(extractProject());
	}

}
