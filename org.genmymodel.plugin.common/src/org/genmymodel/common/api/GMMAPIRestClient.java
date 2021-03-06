package org.genmymodel.common.api;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.genmymodel.common.account.GMMCredential;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
 * @author Ali Gourch
 *
 */
public class GMMAPIRestClient {
	public static final String REAL_API = "https://api.genmymodel.com";
	public static final String OAUTH_TOK = REAL_API  + "/oauth/token";
	public static final String USER_PROJECTS = REAL_API + "/users/{username}/projects";
	public static final String USER_PROJECT = REAL_API + "/projects/{projectID}";
	public static final String USER_SHARED_PROJECTS = REAL_API + "/projects/shared";
	public static final String USER_CUSTOMGENERATORS = REAL_API + "/customgenerators";
	public static final String USER_CUSTOMGENERATOR = REAL_API + "/customgenerators/{generatorID}";
	public static final String COMPILE_RESTURL = REAL_API + "/customgenerators/dev/compile";
	public static final String EXEC_RESTURL_FRAG = REAL_API + "/customgenerators/dev/execute/";
	public static final String USER_IMPORTED_PROJECT = REAL_API + "/projects/import";
	public static final String CUSTOMGEN_CLASSIC_LAUNCH = REAL_API + "/projects/{projectID}/custom/{generatorID}";
	public static final String PROJECT_XMI = USER_PROJECT + "/xmi";
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
	
	public CompilCallResult POSTCustomgenLaunch(String projectID, String customgenID, GMMCredential credential) throws IOException {
		return POST(createOAuthTemplate(credential), CUSTOMGEN_CLASSIC_LAUNCH, projectID, customgenID);
	}
	
	public CompilCallResult POST(RestTemplate template, String url, Object... urlVariables) throws IOException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<String>("",headers);
			
			ResponseEntity<GMMCallResult> res = template.postForEntity(url, entity, GMMCallResult.class, urlVariables);
			if (res.getBody().getOutputUrl() == null) {
				return new CompilCallResult(res.getBody(), null);
			}
			File tmpZip = File.createTempFile("GMM", ".zip");
			FileUtils.copyURLToFile(new URL(res.getBody().getOutputUrl()), tmpZip, 10000, 10000);
			return new CompilCallResult(res.getBody(), tmpZip);
		} catch (HttpStatusCodeException e) {
			GMMCallResult call = new ObjectMapper().readValue(e.getResponseBodyAsString(), GMMCallResult.class);
			return new CompilCallResult(call, null);
		}
	}
	public void PUTCustomGen(CustomGeneratorBinding generator, GMMCredential credential) throws IOException {
		PUT(createOAuthTemplate(credential), USER_CUSTOMGENERATOR, generator, generator.getGeneratorId());
	}
	
	public <T> void PUT(RestTemplate template, String url, T object, Object... urlVariables) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<T> entity = new HttpEntity<T>(object, headers);
		template.put(url, entity, urlVariables);
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
	 * Calls the GenMyModel API in order to import the user project.
	 * @param credential The user credential.
	 * @param project The user project.
	 * @return A ResponseEntity containing the user project informations.
	 */
	public ResponseEntity<ProjectPostBinding> POSTImportedProject(GMMCredential credential, ProjectPostBinding project) {
		ResponseEntity<ProjectPostBinding> response = createOAuthTemplate(credential).postForEntity(USER_IMPORTED_PROJECT, project, ProjectPostBinding.class);
		return response;			
	}

	/**
	 * Calls the GenMyModel API in order to create a new user custom generator.
	 * @param credential The user credential.
	 * @param generator The user generator.
	 * @return A ResponseEntity containing the user generator informations.
	 */
	public ResponseEntity<CustomGeneratorBinding> POSTGenerator(GMMCredential credential, CustomGeneratorBinding generator) {
		try {
			ResponseEntity<CustomGeneratorBinding> response = createOAuthTemplate(credential).postForEntity(USER_CUSTOMGENERATORS, generator, CustomGeneratorBinding.class);
			return response;			
		} catch (HttpStatusCodeException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Calls the GenMyModel API in order to delete the user project.
	 * @param credential The user credential.
	 * @param projectID the project ID.
	 */
	public void DELETEProject(GMMCredential credential, String projectID)
	{
		createOAuthTemplate(credential).delete(USER_PROJECT, projectID);
	}

	/**
	 * Returns user projects.
	 * @param credential The user credential.
	 * @return A tab containing the user project informations.
	 */
	public ProjectBinding[] GETMyProjects(GMMCredential credential) {
		return GET(USER_PROJECTS, ProjectBinding[].class, credential, credential.getUsername());
	}

	/**
	 * Returns shared user projects.
	 * @param credential The user credential.
	 * @return A tab containing the shared user project informations.
	 */
	public ProjectBinding[] GETSharedProjects(GMMCredential credential) {
		return GET(USER_SHARED_PROJECTS, ProjectBinding[].class, credential, credential.getUsername());
	}

	/**
	 * Returns project XMI.
	 * @param credential The user credential.
	 * @param projectID The project ID.
	 * @return A String containing the project XMI.
	 */
	public String GETProjectXMI(GMMCredential credential, String projectID) {
		return GET(PROJECT_XMI, String.class, credential, projectID);
	}

	/**
	 * Returns user custom generators.
	 * @param credential The user credential.
	 * @return A tab containing the user project informations.
	 */
	public CustomGeneratorBinding[] GETMyCustomGenerators(GMMCredential credential) {
		return GET(USER_CUSTOMGENERATORS, CustomGeneratorBinding[].class, credential, credential.getUsername());
	}
	
	/**
	 * Calls the GenMyModel API in order to delete the user generator.
	 * @param credential The user credential.
	 * @param generatorID the generator ID.
	 */
	public void DELETEGenerator(GMMCredential credential, Integer generatorID)
	{
		createOAuthTemplate(credential).delete(USER_CUSTOMGENERATOR, generatorID);
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
