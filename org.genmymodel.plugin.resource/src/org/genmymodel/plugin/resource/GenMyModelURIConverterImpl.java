package org.genmymodel.plugin.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.genmymodel.common.api.GMMAPIRestClient;

/**
 * 
 * @author Vincent Aranega
 * @author Ali Gourch
 *
 */
public class GenMyModelURIConverterImpl extends ExtensibleURIConverterImpl {

	private static Logger logger = Logger.getLogger(GenMyModelURIConverterImpl.class.getName());
	public static final String GENMYMODEL_RESOURCE_URI_PREFIX = "genmymodel://";
	public static final String GENMYMODEL_LIB = "pathmap://GENMYMODEL_LIBRARIES/";
	public static final String UML_LIB = "pathmap://UML_LIBRARIES/";

	@Override
	public InputStream createInputStream(URI uri) throws IOException {
		return createInputStream(uri, null);
	}

	public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException {
		InputStream is = null;

		try {
			is = super.createInputStream(uri, options);
		} catch (Exception e) {
			logger.finest("Unable to create stream from uri = " + uri + ", loading from external source.");
			is = getProjectStreamFromAPI(uri);
		}

		return is;
	}

	private InputStream getProjectStreamFromAPI(URI uri) {
		try {

			GMMAPIRestClient client = GMMAPIRestClient.getInstance();
			return client.GETasInputstream(GMMAPIRestClient.REAL_API + "/projects/" + extractProjectID(uri) + "/data", null); // TODO manage credentials
		} catch (Exception e) { 
			e.printStackTrace();
		}

		return null;
	}

	private String extractProjectID(URI uri) {
		if (uri != null) {
			return uri.toString().replace(UML_LIB, "").replace(GENMYMODEL_LIB, "").replace(GENMYMODEL_RESOURCE_URI_PREFIX, "");
		}

		return null;
	}

}