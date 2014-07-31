package org.genmymodel.plugin.resource;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.change.ChangeDescription;
import org.eclipse.emf.ecore.change.util.ChangeRecorder;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.BinaryResourceImpl;

/**
 * 
 * @author Vincent Aranega
 *
 */
public class GenMyModelResource extends BinaryResourceImpl {
	private ChangeRecorder recorder;
	
	public GenMyModelResource() {
		super();
		//this.recorder = new GenMyModelChangeRecorder(this);
	}
	
	public GenMyModelResource(URI uri) {
		super(uri);
		//this.recorder = new GenMyModelChangeRecorder(this);
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
