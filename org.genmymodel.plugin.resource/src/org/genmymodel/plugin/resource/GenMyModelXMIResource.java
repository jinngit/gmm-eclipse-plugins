package org.genmymodel.plugin.resource;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

public class GenMyModelXMIResource extends XMIResourceImpl {
	protected GenMyModelTracker tracker;
	protected boolean innerLoaded;

	public GenMyModelXMIResource() {
		super();
		//setTrackingModification(true);
	}

	public GenMyModelXMIResource(URI uri) {
		super(uri);
		//setTrackingModification(true);
	}

	@Override
	public void save(Map<?, ?> options) throws IOException {
		// super.save(options); //TODO TMP DISABLE
		if (tracker != null && !tracker.isStreamMode()) {
			tracker.sendCommands(false);
		}
	}

	@Override
	public void load(Map<?, ?> options) throws IOException {
		innerLoaded = false;
		super.load(options);
		tracker = new GenMyModelTracker(this);
		innerLoaded = true;
	}

	@Override
	protected URIConverter getURIConverter() {
		return new GenMyModelURIConverterImpl();
	}

	@Override
	protected void attachedHelper(EObject eObject) {
		if (innerLoaded) {
			tracker.notifyCreate(eObject);
		}
		super.attachedHelper(eObject);
	}

	@Override
	protected void detachedHelper(EObject eObject) {
		if (innerLoaded) {
			tracker.notifyDelete(eObject);
		}
		super.detachedHelper(eObject);
	}
	
	@Override
	protected Adapter createModificationTrackingAdapter() {
		return new GenMyModelInnerTracker();
	}

	public class GenMyModelInnerTracker extends ModificationTrackingAdapter {
		public GenMyModelInnerTracker() {
		}

		@Override
		public void notifyChanged(Notification notification) {
			if (innerLoaded) {
				tracker.notifyModelEvent(notification);
			}
		}
	}
}
