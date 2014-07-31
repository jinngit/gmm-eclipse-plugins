package org.genmymodel.common.api;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class provides facilities to communicate with GenMyModel API in order to
 * compile/execute a custom generator project.
 * 
 * @author Vincent Aranega
 *
 */
public class GMMAPIRestClient {
	public static final String REAL_API = "https://apipreprodks.genmymodel.com"; 
	public static final String OAUTH_TOK = REAL_API  + "/oauth/token";
	public static final String USER_PROJECTS = REAL_API + "/users/{username}/projects";
	public static final String COMPILE_RESTURL = REAL_API + "/customgenerators/dev/compile";
	public static final String EXEC_RESTURL_FRAG = REAL_API + "/customgenerators/dev/execute/";
	private static final String CLIENT_ID = "test";
	private static final String CLIENT_SECRET = "test";

	public static ThreadLocal<GMMAPIRestClient> THREAD_LOCAL = new ThreadLocal<GMMAPIRestClient>() {

		@Override
		protected GMMAPIRestClient initialValue() {
			return createAPIRestClient();
		}

	};

	/**
	 * Prevents external instance creation.
	 */
	private GMMAPIRestClient() {
	}

	/**
	 * Creates a GMMAPIRestClient (singleton value).
	 * @return A new GMMAPIRestClient.
	 */
	protected static GMMAPIRestClient createAPIRestClient() {
		return new GMMAPIRestClient();
	}

	/**
	 * Gets the GMMAPIRestClient instance.
	 * @return The GMMAPIRestClient instance.
	 */
	public static GMMAPIRestClient getInstance() {
		return THREAD_LOCAL.get();
	}

	/**
	 * Calls the GenMyModel API in order to compile the 
	 * custom generator project given as a zip archive.
	 * @param zipArchive The custom generator project to compile.
	 * @return A compilCallResult with information about the compilation.
	 * @throws IOException If an error occurs during unpacking of answered zip.
	 */
	public CompilCallResult POSTCompile(File zipArchive) throws IOException {
		return POST(new RestTemplate(), COMPILE_RESTURL, zipArchive);
	}

	/**
	 * Calls the GenMyModel API in order to execute the compiled custom generator
	 * project given as a zip archive.
	 * @param zipArchive The compiled custom generator project to execute
	 * @param projectID The model project ID on which the generator should be executed.
	 * @param credential The user credential to access the projectID.
	 * @return A CompilationCallResult with information about the execution.
	 * @throws IOException If an error occurs during unpacking of the answered zip.
	 */
	public CompilCallResult POSTExec(File zipArchive, String projectID, GMMCredential credential) throws IOException {
		return POST(createOAuthTemplate(credential), EXEC_RESTURL_FRAG + projectID, zipArchive);
	}

	private CompilCallResult POST(RestTemplate template, String url, File zipArchive) throws IOException {
		Resource resource = new FileSystemResource(zipArchive);
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE);
		parts.add("file", resource);

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

	/**
	 * Returns user projects.
	 * @param credential The user credential.
	 * @return A tab containing the user project information.
	 */
	public ProjectBinding[] GETMyProjects(GMMCredential credential) {
		return GET(USER_PROJECTS, ProjectBinding[].class, credential, credential.getUsername());
	}

	/**
	 * Performs a GET call on a given address.
	 * @param url The URL to call.
	 * @param clazz The answer type.
	 * @param credential The user credential.
	 * @param params Additional parameter that could be part of the URL.
	 * @return An answer of clazz type.
	 */
	public <T> T GET(String url, Class<T> clazz, GMMCredential credential, Object... params) {
		RestTemplate template = createOAuthTemplate(credential);
		ResponseEntity<T> response = template.getForEntity(url, clazz, params);
		return response.getBody();
	}
	
	/**
	 * Performs a GET call on a given address and return the body as an InputStream.
	 * @param url The URL to call.
	 * @param credential The user credential, if null, a non oauth rest template is used.
	 * @param params Additional parameter that could be part of the URL.
	 * @return An InputStream.
	 */
	public InputStream GETasInputstream(String url, GMMCredential credential, Object... params) {
		RestTemplate template = credential != null ? createOAuthTemplate(credential): new RestTemplate();
		ResponseEntity<byte[]> res = template.getForEntity(url, byte[].class, params);
		return new ByteArrayInputStream(res.getBody());
	}

	/**
	 * Creates an OAuth2 rest template that provides facilities to deal with OAuth2 auth.
	 * @param credential The user credential.
	 * @return A new OAut2 Rest Template.
	 */
	protected RestTemplate createOAuthTemplate(GMMCredential credential) {
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

	/**
	 * Represents a compilation/execution wrapper.
	 * 
	 * @author Vincent Aranega
	 *
	 */
	public class CompilCallResult {
		/**
		 * Compilation call result.
		 */
		public GMMCallResult callResult;
		/**
		 * Produced zip file downloaded if the call result has no error. If an
		 * error occured during compilation/execution, this variable is null.
		 */
		public File zip;

		public CompilCallResult(GMMCallResult res, File zip) {
			this.callResult = res;
			this.zip = zip;
		}
	}

}
