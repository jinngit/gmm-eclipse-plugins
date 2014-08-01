package org.genmymodel.plugin.resource;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.change.ChangeDescription;
import org.eclipse.emf.ecore.change.util.ChangeRecorder;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

public class GenMyModelXMIResource extends XMIResourceImpl {
private ChangeRecorder recorder;
	
	public GenMyModelXMIResource() {
		super();
	}
	
	public GenMyModelXMIResource(URI uri) {
		super(uri);
	}
	
	@Override
	public void save(Map<?, ?> options) throws IOException {
		//super.save(options); //TODO TMP DISABLE
		ChangeDescription desc = getChangeRecorder().summarize();
		
		System.out.println(desc);
	}
	
	@Override
	public void load(Map<?, ?> options) throws IOException {
		super.load(options);
		this.recorder = new GenMyModelChangeRecorder(getResourceSet());
	}
	
	@Override
	protected URIConverter getURIConverter() {
		return new GenMyModelURIConverterImpl();
	}	
	
	protected ChangeRecorder getChangeRecorder() {
		return this.recorder;
	}
}
