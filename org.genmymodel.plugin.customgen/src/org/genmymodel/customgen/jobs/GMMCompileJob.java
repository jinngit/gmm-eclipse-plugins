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
import org.genmymodel.customgen.api.GMMAPIRestClient.CompilCallResult;
import org.genmymodel.customgen.project.GenMyModelProject;
import org.springframework.web.client.RestClientException;

/**
 * Compilation job. This class create an eclipse Job that launches the
 * compilation process.
 * 
 * @author Vincent Aranega
 *
 */
public class GMMCompileJob extends GMMCustomGenJob {

	/**
	 * Creates a compile job.
	 * @param name The compilation job name.
	 * @param project The GenMyModel project.
	 */
	public GMMCompileJob(String name, GenMyModelProject project) {
		super(name, project);
	}

	/**
	 * {@inheritDoc}
	 * Calls the API compile service.
	 */
	protected IStatus apiCall(File zip, IProgressMonitor monitor) {
		monitor.subTask(String.format(GMMJobMessages.TASK_APICALL, "compilation"));
		CompilCallResult res = null;
		try {
			res = GMMAPIRestClient.getInstance().POSTCompile(zip);
		} catch (IOException e) {
			return blockError(GMMJobMessages.ERROR_APIFETCH, e);
		} catch (RestClientException e) {
			return blockError(GMMJobMessages.ERROR_APICALL, e);
		}
		monitor.worked(3);

		if (res.callResult.hasErrors()) {
			return nonblockError(res.callResult.getErrors());
		}

		monitor.subTask(GMMJobMessages.TASK_COMPILERES);
		try {
			ZipFile compZip = new ZipFile(res.zip);
			compZip.extractAll(new File(project.getIProject().getLocationURI())
					.getAbsolutePath());
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
