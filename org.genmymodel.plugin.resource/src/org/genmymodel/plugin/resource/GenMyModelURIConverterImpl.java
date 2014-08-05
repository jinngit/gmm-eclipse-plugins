package org.genmymodel.plugin.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.genmymodel.common.account.GMMKeyStore;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream createInputStream(URI uri) throws IOException {
		return createInputStream(uri, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
			GMMKeyStore keyStore = GMMKeyStore.getInstance();
			return client.GETasInputstream(GMMAPIRestClient.REAL_API + "/projects/" + extractProjectID(uri) + "/data", keyStore.getCurrentCredential());
		} catch (Exception e) { 
			e.printStackTrace();
		}

		return null;
	}

	private String extractProjectID(URI uri) {
		if (uri != null) {
			return uri.toString().replace(GENMYMODEL_RESOURCE_URI_PREFIX, "");
		}

		return null;
	}

}