
package org.genmymodel.plugin.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;

/**
 * 
 * @author Vincent Aranega
 *
 */
public class GenMyModelXMIResourceFactory extends ResourceFactoryImpl implements Resource.Factory {
	
	public GenMyModelXMIResourceFactory() {
		super();
	}
	
	@Override
	public Resource createResource(URI uri) {
		return new GenMyModelXMIResource(uri);
	}
}
