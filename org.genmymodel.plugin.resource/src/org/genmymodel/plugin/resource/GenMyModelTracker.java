package org.genmymodel.plugin.resource;

import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;

public class GenMyModelTracker implements CommandStackListener {
	protected boolean streamMode = false;
	protected boolean nextIsDelete = false;
	protected boolean nextIsCreate = false;
	protected List<Notification> notifications;

	public GenMyModelTracker(Resource resource) {
		EditingDomain domain = getEditingDomain(resource);
		if (domain != null && domain.getCommandStack() != null) {
			this.streamMode = true;
			domain.getCommandStack().addCommandStackListener(this);
		}
		this.notifications = new LinkedList<Notification>();
	}

	@Override
	public void commandStackChanged(EventObject event) {
		sendCommands();
	}
	
	public void sendCommands() {
		System.out.println("COMMAND(S) IS/ARE OK, SENDING IT/THEM");
		for (Notification notification : notifications) {
			System.out.println(" +-- " + notification);
		}
		notifications.clear();
	}
	
	protected void storeCreate(Notification notification) {
		System.out.println("CREATES A CREATE COMMAND AND ADD IT");
		notifications.add(notification);
	}
	
	protected void storeDelete(Notification notification) {
		System.out.println("CREATES A DELETE COMMAND AND ADD IT");
		notifications.add(notification);
	}
	
	protected void storeCommand(Notification notification) {
		System.out.println("CREATES A (COMPOUND) COMMAND AND ADD IT");
		notifications.add(notification);
	}
	
	
	public void notifyModelEvent(Notification notification) {
		if (!notification.isTouch()) {
			if (nextIsCreate && isStreamMode()) {
				storeCreate(notification);
			} else if (nextIsDelete && isStreamMode()) {
				storeDelete(notification);
			} else {
				storeCommand(notification);
			}
			nextIsCreate = false;
			nextIsDelete = false;
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
