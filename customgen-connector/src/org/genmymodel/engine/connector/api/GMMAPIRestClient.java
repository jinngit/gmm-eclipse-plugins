package org.genmymodel.engine.connector.api;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

public class GMMAPIRestClient {
	public static final String API_URL = "https://127.0.0.1:8443/engine";
	public static final String COMPILE_RESTURL = API_URL + "/mtl/compile";
	private RestTemplate template;

	public static ThreadLocal<GMMAPIRestClient> THREAD_LOCAL = new ThreadLocal<GMMAPIRestClient>() {

		@Override
		protected GMMAPIRestClient initialValue() {
			return createAPIRestClient();
		}

	};

	public GMMAPIRestClient() {
		template = new RestTemplate(new DisableSSLHttpRequestFactory());
		template.getMessageConverters().add(new FormHttpMessageConverter());
		template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		template.setErrorHandler(new GenerationErrorHandler());
	}

	public static Logger LOG = Logger.getLogger(GMMAPIRestClient.class
			.getName());

	protected static GMMAPIRestClient createAPIRestClient() {
		return new GMMAPIRestClient();
	}

	public static GMMAPIRestClient getInstance() {
		return THREAD_LOCAL.get();
	}

	public CompilCallResult POSTCompile(File zipArchive) {
		Resource resource = new FileSystemResource(zipArchive);
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE);
		parts.add("file", resource);

		HttpEntity<GMMCallResult> res = template.exchange(COMPILE_RESTURL, HttpMethod.POST,
				new HttpEntity<MultiValueMap<String, Object>>(parts),
				GMMCallResult.class);
		
		if (((GenerationErrorHandler)template.getErrorHandler()).getStatus() != HttpStatus.OK) {
			return new CompilCallResult(res.getBody(), null);
		} else {
			return new CompilCallResult(res.getBody(), null); // TODO
		}
	}
	
	
	public class CompilCallResult {
		public GMMCallResult callResult;
		public File zip;
		
		public CompilCallResult(GMMCallResult res, File zip) {
			this.callResult = res;
			this.zip = zip;
		}
	}

	/**
	 * Simple RequestFactory disabling ssl handshake for rest template in
	 * context of communication between engine and api.
	 * 
	 * @author Vincent Aranega
	 */
	public class DisableSSLHttpRequestFactory extends
	SimpleClientHttpRequestFactory {

		private final HostnameVerifier verifier;

		public DisableSSLHttpRequestFactory() {
			this.verifier = new DummyHostnameVerifier();
		}

		@Override
		protected void prepareConnection(HttpURLConnection connection,
				String httpMethod) throws IOException {
			if (connection instanceof HttpsURLConnection) {
				((HttpsURLConnection) connection).setHostnameVerifier(verifier);
				((HttpsURLConnection) connection)
				.setSSLSocketFactory(((DummyHostnameVerifier) verifier)
						.getSSLContext().getSocketFactory());
			}
			super.prepareConnection(connection, httpMethod);
		}

	}

	/**
	 * Dummy hostname verifier
	 * 
	 * @author Vincent Aranega
	 */
	public class DummyHostnameVerifier implements HostnameVerifier {

		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		public SSLContext getSSLContext() {
			SSLContext sslContext = null;
			try {
				sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, trustAllCerts,
						new java.security.SecureRandom());
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (KeyManagementException e) {
				e.printStackTrace();
			}
			return sslContext;
		}

		public boolean verify(String hostname, SSLSession session) {
			System.out.println("Verifying " + hostname);
			return true;
		}
	}

	/**
	 * GenerationErrorHandler - This class is used to handle error
	 * for non OK http response status.
	 * @author Vincent Aranega
	 */
	class GenerationErrorHandler implements ResponseErrorHandler
	{
		private HttpStatus	status;
		private boolean		errors;

		@Override
		public boolean hasError(ClientHttpResponse response) throws IOException
		{
			this.errors = !HttpStatus.OK.equals(response.getStatusCode());
			return this.errors;
		}

		@Override
		public void handleError(ClientHttpResponse response) throws IOException
		{
			this.setResponse(response);
		}

		public HttpStatus getStatus()
		{
			return status;
		}

		public void setResponse(ClientHttpResponse response) throws IOException
		{
			this.status = response.getStatusCode();
		}

		public boolean hasErrors()
		{
			return this.errors;
		}
	}

}
