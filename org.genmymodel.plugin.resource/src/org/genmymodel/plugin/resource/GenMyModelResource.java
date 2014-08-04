package org.genmymodel.plugin.resource;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.BinaryResourceImpl;

/**
 * 
 * @author Vincent Aranega
 *
 */
public class GenMyModelResource extends BinaryResourceImpl {
	
	protected GenMyModelTracker tracker;
	protected boolean innerLoaded;

	public GenMyModelResource() {
		super();
		setTrackingModification(true);
	}

	public GenMyModelResource(URI uri) {
		super(uri);
		setTrackingModification(true);
	}

	@Override
	public void save(Map<?, ?> options) throws IOException {
		// super.save(options); //TODO TMP DISABLE
		if (tracker != null && !tracker.isStreamMode()) {
			tracker.sendCommands();
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
	public void attached(EObject eObject) {
		if (innerLoaded) {
			tracker.notifyCreate(eObject);
		}
		super.attached(eObject);
	}

	@Override
	public void detached(EObject eObject) {
		if (innerLoaded) {
			tracker.notifyDelete(eObject);
		}
		super.detached(eObject);
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
