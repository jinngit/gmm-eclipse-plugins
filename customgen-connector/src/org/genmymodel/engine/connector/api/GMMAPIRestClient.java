package org.genmymodel.engine.connector.api;

import java.io.File;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class GMMAPIRestClient {
	public static final String API_URL = "https://127.0.0.1:8443/engine";
	public static final String COMPILE_RESTURL = API_URL + "/mtl/compile";

	public static ThreadLocal<GMMAPIRestClient> THREAD_LOCAL = new ThreadLocal<GMMAPIRestClient>() {

		@Override
		protected GMMAPIRestClient initialValue() {
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
			{
				public boolean verify(String hostname, SSLSession session)
				{
					System.out.println("Verifying "  + hostname);
					// ip address of the service URL(like 127.0.0.1)
					if (hostname.equals("127.0.0.1")) {
						return true;
					}
					return false;
				}
			});
			return createAPIRestClient();
		}

	};

	public static Logger LOG = Logger.getLogger(GMMAPIRestClient.class
			.getName());

	protected static GMMAPIRestClient createAPIRestClient() {
		return new GMMAPIRestClient();
	}

	public static GMMAPIRestClient getInstance() {
		return THREAD_LOCAL.get();
	}

	public void POSTCompile(File zipArchive) {
		RestTemplate template = new RestTemplate();
		template.getMessageConverters().add(new FormHttpMessageConverter());
		SSLSocketFactory sf;

		Resource resource = new FileSystemResource(zipArchive);
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE);
		parts.add("file", resource);
		template.exchange(COMPILE_RESTURL, HttpMethod.POST,
				new HttpEntity<MultiValueMap<String, Object>>(parts),
				String.class);
	}

}
