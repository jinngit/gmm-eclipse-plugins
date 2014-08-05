package org.genmymodel.plugin.resource;

import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.change.util.ChangeRecorder;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.DeleteCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;

public class GenMyModelTracker extends ChangeRecorder implements
		CommandStackListener {
	protected boolean streamMode = false;
	protected boolean nextIsDelete = false;
	protected boolean nextIsCreate = false;
	protected LinkedList<Notification> notifications;
	protected Resource resource;
	
	public static int CREATE = Notification.EVENT_TYPE_COUNT + 1;

	public GenMyModelTracker(Resource resource) {
		super(resource.getResourceSet());
		EditingDomain domain = getEditingDomain(resource);
		if (domain != null && domain.getCommandStack() != null) {
			this.streamMode = true;
			domain.getCommandStack().addCommandStackListener(this);
		}
		this.notifications = new LinkedList<Notification>();
		this.resource = resource;
	}

	@Override
	protected void handleResource(Notification notification) {
		if (!notification.isTouch()) {
			EObject e = null;
			if (notification.getNotifier() instanceof EObject) {
				e = (EObject) notification.getNotifier();
			}
			if (notification.getNewValue() instanceof EObject) {
				e = (EObject) notification.getNewValue();
			}

			if (e != null && !e.eAdapters().contains(this)) {
				handleCreate(notification);
			}
		}

		super.handleResource(notification);

		if (notification.isTouch()) { // Skip resolve
			return;
		}
	}

	@Override
	protected void handleFeature(EStructuralFeature feature,
			EReference containment, Notification notification, EObject eObject) {

		if (!notification.isTouch()) {
			EObject e = null;
			if (notification.getNotifier() instanceof EObject) {
				e = (EObject) notification.getNotifier();
			}
			if (notification.getNewValue() instanceof EObject) {
				e = (EObject) notification.getNewValue();
			}

			if (e != null && !e.eAdapters().contains(this)) {
				handleCreate(notification);
				System.out.println("CHANGE ON " + notification);
			}
		}

		super.handleFeature(feature, containment, notification, eObject);

		if (notification.isTouch()) { // Skip resolve
			return;
		}
		if (nextIsDelete && !isStreamMode()) {
			handleDelete();
			System.out.println("CHANGE ON " + notification);
		}
		notifications.add(notification);
	}

	@Override
	public void commandStackChanged(EventObject event) {
		CommandStack commandStack = (CommandStack) event.getSource();
		Command c = commandStack.getMostRecentCommand();
		if (c instanceof DeleteCommand) {
			handleDelete();
		}

		sendCommands(false);
	}

	public void sendCommands(boolean handledDelete) {
		System.out.println("COMMAND(S) IS/ARE OK, SENDING IT/THEM");

		if (isStreamMode() && handledDelete) {
			for (int i = notifications.size() - 1; i >= 0; i--) {
				System.out.println(" +-- " + notifications.get(i));
			}
		} else {

			for (Notification notification : notifications) {
				System.out.println(" +-- " + notification);
			}
		}
		notifications.clear();
	}

	protected void handleCreate(Notification notification) {
		System.out.println("HANDLE A CREATE COMMAND AND ADD IT");
		EClass metaclass = ((EObject)notification.getNewValue()).eClass();
		notifications.add(new ENotificationImpl(null, CREATE, null, metaclass.getName(), EcoreUtil.generateUUID())); // Hacked notification
	}

	protected void handleDelete() {
		if (isStreamMode()) {
			System.out.println("PREVIOUS COMMAND IS A DELETE " + notifications.getLast());
			this.nextIsDelete = true;
		} else {
			System.out.println("NEXT COMMAND WILL BE A DELETE ");
			this.nextIsDelete = false;
		}
		
	}

	protected void storeCommand(Notification notification) {
		System.out.println("CREATES A (COMPOUND) COMMAND AND ADD IT");
		notifications.add(notification);
	}

	public void notifyModelEvent(Notification notification) {
		if (!notification.isTouch()) {
			storeCommand(notification);
			if (!isStreamMode() && nextIsCreate) {
				// handleCreate();
			} else if (!isStreamMode() && nextIsDelete) {
				handleDelete();
			}

		}
	}

	public void notifyCreate(EObject e) {
		this.nextIsCreate = true;
		this.nextIsDelete = false;
	}

	public void notifyDelete(EObject e) {
		this.nextIsCreate = false;
		this.nextIsDelete = true;
	}

	public boolean isStreamMode() {
		return this.streamMode;
	}

	public static EditingDomain getEditingDomain(Resource resource) {
		// Copy
		EditingDomain domain = null;
		IEditingDomainProvider editingDomainProvider = (IEditingDomainProvider) EcoreUtil
				.getExistingAdapter(resource, IEditingDomainProvider.class);
		if (editingDomainProvider != null) {
			domain = editingDomainProvider.getEditingDomain();
		} else {
			ResourceSet resourceSet = resource.getResourceSet();
			if (resourceSet instanceof IEditingDomainProvider) {
				EditingDomain editingDomain = ((IEditingDomainProvider) resourceSet)
						.getEditingDomain();
				domain = editingDomain;
			} else if (resourceSet != null) {
				editingDomainProvider = (IEditingDomainProvider) EcoreUtil
						.getExistingAdapter(resourceSet,
								IEditingDomainProvider.class);
				if (editingDomainProvider != null) {
					domain = editingDomainProvider.getEditingDomain();
				}
			}
		}
		return domain;
	}
}
