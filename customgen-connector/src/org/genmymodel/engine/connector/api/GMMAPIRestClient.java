package org.genmymodel.engine.connector.api;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Vincent Aranega
 *
 */
public class GMMAPIRestClient {
	public static final String API_URL = "https://enginepreprodks.genmymodel.com";
	public static final String REAL_API = "https://apipreprodks.genmymodel.com"; // https://apipreprodks.genmymodel.com
	public static final String OAUTH_TOK = REAL_API  + "/oauth/token";
	public static final String USER_PROJECTS = REAL_API + "/users/{username}/projects";
	public static final String COMPILE_RESTURL = API_URL + "/mtl/compile";
	public static final String EXEC_RESTURL_FRAG = API_URL + "/mtl/exec/";
	private static final String CLIENT_ID = "test";
	private static final String CLIENT_SECRET = "test";
	//private RestTemplate template;

	public static ThreadLocal<GMMAPIRestClient> THREAD_LOCAL = new ThreadLocal<GMMAPIRestClient>() {

		@Override
		protected GMMAPIRestClient initialValue() {
			return createAPIRestClient();
		}

	};

	public GMMAPIRestClient() {
		//		template = new RestTemplate(new DisableSSLHttpRequestFactory());
		//		template.getMessageConverters().add(new FormHttpMessageConverter());
		//		template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		//		template.setErrorHandler(new GenerationErrorHandler());
	}

	public static Logger LOG = Logger.getLogger(GMMAPIRestClient.class
			.getName());

	protected static GMMAPIRestClient createAPIRestClient() {
		return new GMMAPIRestClient();
	}

	public static GMMAPIRestClient getInstance() {
		return THREAD_LOCAL.get();
	}

	public CompilCallResult POSTCompile(File zipArchive, GMMCredential credential) throws IOException {
		return POST(COMPILE_RESTURL, zipArchive, credential);
	}

	/**
	 * 
	 * @author Vincent Aranega
	 *
	 */
	public class CompilCallResult {
		public GMMCallResult callResult;
		public File zip;

		public CompilCallResult(GMMCallResult res, File zip) {
			this.callResult = res;
			this.zip = zip;
		}
	}

	public CompilCallResult POSTExec(File zipArchive, String projectID, GMMCredential credential) throws IOException {
		return POST(EXEC_RESTURL_FRAG + projectID, zipArchive, credential);
	}

	private CompilCallResult POST(String url, File zipArchive, GMMCredential credential) throws IOException {
		Resource resource = new FileSystemResource(zipArchive);
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE);
		parts.add("file", resource);

		RestTemplate template = createTemplate(credential);

		try {
			ResponseEntity<GMMCallResult> res = template.exchange(url, HttpMethod.POST,
					new HttpEntity<MultiValueMap<String, Object>>(parts),
					GMMCallResult.class);
			File tmpZip = File.createTempFile("GMM", ".zip");
			FileUtils.copyURLToFile(new URL(res.getBody().getOutputUrl()), tmpZip, 10000, 10000);
			return new CompilCallResult(res.getBody(), tmpZip);
		} catch (HttpStatusCodeException e) {
			GMMCallResult call = new ObjectMapper().readValue(e.getResponseBodyAsString(), GMMCallResult.class);
			return new CompilCallResult(call, null);
		}
	}

	public ProjectBinding[] GETMyProjects(GMMCredential credential) {
		return GET(USER_PROJECTS, ProjectBinding[].class, credential, credential.getUsername());
	}

	public <T> T GET(String url, Class<T> clazz, GMMCredential credential, Object... params) {
		RestTemplate template = createTemplate(credential);
		ResponseEntity<T> response = template.getForEntity(url, clazz, params);
		return response.getBody();
	}

	protected RestTemplate createTemplate(GMMCredential credential) {
		ResourceOwnerPasswordResourceDetails details = new ResourceOwnerPasswordResourceDetails();
		details.setClientId(CLIENT_ID);
		details.setClientSecret(CLIENT_SECRET);
		details.setUsername(credential.getUsername());
		details.setPassword(credential.getPassword());
		details.setAccessTokenUri(OAUTH_TOK);

		OAuth2RestTemplate template = new OAuth2RestTemplate(details);
		template.getMessageConverters().add(new FormHttpMessageConverter());
		template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		return template;
	}

}
