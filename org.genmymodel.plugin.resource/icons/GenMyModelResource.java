package org.genmymodel.plugin.resource;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.BinaryResourceImpl;

/**
 * 
 * @author Vincent Aranega
 *
 */
public class GenMyModelResource extends BinaryResourceImpl {
	
	public GenMyModelResource() {
	}
	
	public GenMyModelResource(URI uri) {
		super(uri);
	}
	
	@Override
	public void save(Map<?, ?> options) throws IOException {
		throw new UnsupportedOperationException("Models cannot be change by eclipse editor ATM. Wait for next versions. ");
		// super.save(options); // TODO Add write support
	}
	
	@Override
	protected URIConverter getURIConverter() {
		return new GenMyModelURIConverterImpl();
	}	
}
