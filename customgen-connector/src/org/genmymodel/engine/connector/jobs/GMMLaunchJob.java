package org.genmymodel.engine.connector.jobs;

import java.io.File;
import java.io.IOException;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.genmymodel.engine.connector.api.GMMAPIRestClient;
import org.genmymodel.engine.connector.api.GMMAPIRestClient.CompilCallResult;
import org.genmymodel.engine.connector.api.GMMCredential;
import org.genmymodel.engine.connector.project.GenMyModelProject;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.client.RestClientException;

public class GMMLaunchJob extends GMMCustomGenJob {

	public GMMLaunchJob(String name, GenMyModelProject project) {
		super(name, project);
	}

	protected IStatus apiCall(File zip, IProgressMonitor monitor) {
		monitor.subTask("Calling GenMyModel API custom generator launch service...");
		CompilCallResult res = null;
		try {
			try {
				res = GMMAPIRestClient.getInstance().POSTExec(zip, "_tiPjsFdaEDCmJtgCDSWzkw", new GMMCredential("","")); // TODO
			} catch (IOException e) {
				return blockError(
						"Error while fetching generation result",
						e);
			}
		} catch (OAuth2Exception e) {
			return blockError(
					"Wrong credentials, your username or pass is not good.\nYou have to "
					+ "use a user/pass credential (github and google+ authentications are not supported).",
					e);
		} catch (RestClientException e) {
			return blockError(
					"Error during service call. If you are connected to the internet, please contact support.",
					e);
		}
		monitor.worked(3);
		
		if (res.callResult.hasErrors()) {
			return nonblockError(res.callResult.getErrors());
		}
		
		monitor.subTask("Dispatching compilation results");
		try {
			ZipFile compZip = new ZipFile(res.zip);
			compZip.extractAll(new File(project.getIProject().getLocationURI()).getAbsolutePath());
			FileUtils.forceDelete(res.zip);
		} catch (ZipException e) {
			return blockError(
					"Error while dispatching compilation result.",
					e);
		} catch (IOException e) {
			nonBlockWarning("Cannot delete '"
					+ res.zip.getAbsolutePath()
					+ "' temp directory. You should delete it by yourself.", e);
		}
		monitor.worked(2);
		
		return Status.OK_STATUS;
	}


}
