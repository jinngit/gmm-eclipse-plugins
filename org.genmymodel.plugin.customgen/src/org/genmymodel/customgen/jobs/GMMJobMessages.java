package org.genmymodel.customgen.jobs;

public interface GMMJobMessages {
	public static final String ERROR_APICALL = "Error during service call. If you are connected to the internet, please contact support.";
	public static final String ERROR_APIFETCH = "Error while fetching generation result.";
	public static final String ERROR_APIDISPATCH = "Error while dispatching result.";
	public static final String ERROR_DELETE = "Cannot delete '%s'. You should delete it by yourself.";
	public static final String ERROR_OAUTH = "Wrong credentials, your username or pass is not good.\nYou have to "
											+ "use a user/pass credential (github and google+ authentications are not supported).";
	public static final String ERROR_ZIPPREP = "Error while preparing your projet archive! Did you have right to write in '%s' tmp folder?";
	public static final String ERROR_REFRESH = "Error while refreshing your project.";
	
	public static final String TASK_APICALL = "Calling GenMyModel API custom generator %s service...";
	public static final String TASK_EXECRES = "Dispatching launch results...";
	public static final String TASK_COMPILERES = "Dispatching compilation results...";
	public static final String TASK_PREPAREZIP = "Preparing project archive...";
	public static final String TASK_COMPEXEC = "Compiling/executing custom generator project...";
	public static final String TASK_CLEANTMP = "Cleaning tmp folders...";
}
