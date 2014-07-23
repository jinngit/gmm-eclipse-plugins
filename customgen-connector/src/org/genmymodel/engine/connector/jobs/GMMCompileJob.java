package org.genmymodel.engine.connector.jobs;

import java.io.File;
import java.io.IOException;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.genmymodel.engine.connector.api.GMMAPIRestClient;
import org.genmymodel.engine.connector.api.GMMAPIRestClient.CompilCallResult;
import org.genmymodel.engine.connector.handlers.GMMAbstractHandler;
import org.genmymodel.engine.connector.project.IGenMyModelProject;
import org.springframework.web.client.RestClientException;

public class GMMCompileJob extends GMMCustomGenJob {

	public GMMCompileJob(String name, IGenMyModelProject project) {
		super(name, project);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Zipping custom generator project", 7);
		File zip = null;
		try {
			zip = project.zipMe();
		} catch (Exception e) {
			return blockError(
					"Error while zipping your projet! Did you have right to write in '"
							+ GMMAbstractHandler.systemTmpFolder
							+ "' tmp folder?", e);
		}
		monitor.worked(1);

		if (zip == null) {
			return Status.OK_STATUS;
		}
		
		IStatus subw = apiCall(zip, monitor);
		if (subw != Status.OK_STATUS) {
			return subw;
		}
		
		monitor.subTask("Cleaning tmp folders");
		try {
			FileUtils.forceDelete(zip.getParentFile());
		} catch (IOException e) {
			nonBlockWarning("Cannot delete '"
					+ zip.getParentFile().getAbsolutePath()
					+ "' temp directory. You should delete it by yourself.", e);
		}
		monitor.worked(1);
		
		try {
			project.getIProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (CoreException e) {
			return blockError(
					"Error while refreshing your project.",
					e);
		}

		return Status.OK_STATUS;
	}
	
	protected IStatus apiCall(File zip, IProgressMonitor monitor) {
		monitor.subTask("Calling GenMyModel API compilation URL");
		CompilCallResult res = null;
		try {
			try {
				res = GMMAPIRestClient.getInstance().POSTCompile(zip);
			} catch (IOException e) {
				return blockError(
						"Error while fetching compilation result",
						e);
			}
		} catch (RestClientException e) {
			return blockError(
					"Error during service call. If you are connected to the internet, please contact support",
					e);
		}
		monitor.worked(3);
		
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
