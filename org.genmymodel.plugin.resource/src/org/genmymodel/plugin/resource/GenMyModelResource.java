package org.genmymodel.plugin.resource;

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
	protected URIConverter getURIConverter() {
		return new GenMyModelURIConverterImpl();
	}	
}
