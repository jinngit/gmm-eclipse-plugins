package org.genmymodel.customgen.jobs;

import java.io.File;
import java.io.IOException;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.genmymodel.customgen.api.GMMAPIRestClient;
import org.genmymodel.customgen.api.GMMCredential;
import org.genmymodel.customgen.api.GMMAPIRestClient.CompilCallResult;
import org.genmymodel.customgen.project.GenMyModelProject;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.client.RestClientException;

/**
 * 
 * @author Vincent Aranega
 * @author Ali Gourch
 *
 */
public class GMMLaunchJob extends GMMCustomGenJob {
	private String modelID;
	private GMMCredential credential;
	
	/**
	 * Creates a launch job.
	 * @param name The launch job name.
	 * @param project The GenMyModel project.
	 */
	public GMMLaunchJob(String name, GenMyModelProject project, String modelID, GMMCredential credential) {
		super(name, project);
		this.credential = credential;
		this.modelID = modelID;
	}

	/**
	 * {@inheritDoc}
	 * Calls the API launch service.
	 */
	protected IStatus apiCall(File zip, IProgressMonitor monitor) {
		monitor.subTask(String.format(GMMJobMessages.TASK_APICALL, "launch"));
		CompilCallResult res = null;
		try {
			try {
				res = GMMAPIRestClient.getInstance().POSTExec(zip, modelID, credential);
			} catch (IOException e) {
				return blockError(GMMJobMessages.ERROR_APIFETCH, e);
			}
		} catch (OAuth2Exception e) {
			return blockError(GMMJobMessages.ERROR_OAUTH, e);
		} catch (RestClientException e) {
			return blockError(GMMJobMessages.ERROR_APICALL, e);
		}
		monitor.worked(3);
		
		if (res.callResult.hasErrors()) {
			return nonblockError(res.callResult.getErrors());
		}
		
		monitor.subTask(GMMJobMessages.TASK_EXECRES);
		try {
			ZipFile compZip = new ZipFile(res.zip);
			compZip.extractAll(new File(project.getIProject().getLocationURI()).getAbsolutePath());
			FileUtils.forceDelete(res.zip);
		} catch (ZipException e) {
			return blockError(GMMJobMessages.ERROR_APIDISPATCH, e);
		} catch (IOException e) {
			nonBlockWarning(String.format(GMMJobMessages.ERROR_DELETE, res.zip.getAbsolutePath()), e);
		}
		monitor.worked(2);
		
		return Status.OK_STATUS;
	}


}
