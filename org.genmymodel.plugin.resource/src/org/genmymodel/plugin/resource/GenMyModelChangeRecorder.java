package org.genmymodel.plugin.resource;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.change.ChangeDescription;
import org.eclipse.emf.ecore.change.util.ChangeRecorder;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class GenMyModelChangeRecorder extends ChangeRecorder {

	private static final String[] NOTIFS = { "CREATE", "SET", "UNSET", "ADD",
			"REMOVE", "ADD_MANY", "REMOVE_MANY", "MOVE", "REMOVING_ADAPTER",
			"RESOLVE" };

	public GenMyModelChangeRecorder(Resource resource) {
		super(resource);
	}

	public GenMyModelChangeRecorder(ResourceSet rSet) {
		super(rSet);
	}

	@Override
	protected void handleFeature(EStructuralFeature feature,
			EReference containment, Notification notification, EObject eObject) {

		if (notification.getNewValue() instanceof EObject
				&& notification.getEventType() != Notification.RESOLVE) {
			EObject e = (EObject) notification.getNewValue();
			if (!e.eAdapters().contains(this)) {
				e.eAdapters().add(this);
				System.out.println("CREATE " + e);
			}
		}

		super.handleFeature(feature, containment, notification, eObject);

		if (notification.isTouch()) { // Skip resolve
			return;
		}
		

		if (notification.getEventType() == Notification.REMOVE) {
			EObject rem = (EObject) notification.getOldValue();
			ChangeDescription descri = summarize();
			if (descri.getObjectsToAttach().contains(rem)) {
				System.out.println("DELETE " + rem);
				System.out.println(" EEEEEE " + eObject.eResource().getResourceSet().getAdapterFactories());
			}
		}

		System.out.println("\nCHANGE ON " + eObject);
		System.out.println(" += FEAT " + feature);
		System.out.println(" += CONT " + containment);
		System.out.println(" += NOTI " + NOTIFS[notification.getEventType()]);
		System.out.println(" += ORIG " + notification.getOldValue());
		System.out.println(" += NEW  " + notification.getNewValue());

	}

}
