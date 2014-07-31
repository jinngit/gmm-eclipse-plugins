package org.genmymodel.plugin.resource;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.change.util.ChangeRecorder;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

public class GenMyModelChangeRecorder extends ChangeRecorder {

	private static final String[] NOTIFS = { "CREATE", "SET", "UNSET", "ADD",
			"REMOVE", "ADD_MANY", "REMOVE_MANY", "MOVE", "REMOVING_ADAPTER", "RESOLVE"};
	
	public GenMyModelChangeRecorder(Resource resource) {
		super(resource);
	}

	public GenMyModelChangeRecorder(ResourceSet rSet) {
		super(rSet);
	}

	@Override
	protected void handleFeature(EStructuralFeature feature,
			EReference containment, Notification notification, EObject eObject) {
		
		super.handleFeature(feature, containment, notification, eObject);
		System.out.println("\nCHANGE ON " + eObject);
		System.out.println(" += FEAT " + feature);
		System.out.println(" += CONT " + containment);
		System.out.println(" += NOTI " + NOTIFS[notification.getEventType()]);
		System.out.println(" += ORIG " + notification.getOldValue());
		System.out.println(" += NEW  " + notification.getNewValue());
	}

}
