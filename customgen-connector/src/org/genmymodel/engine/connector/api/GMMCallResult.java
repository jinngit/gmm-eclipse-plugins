package org.genmymodel.engine.connector.api;

import java.util.List;
import java.util.Map;

/**
 * @author Vincent Aranega
 */
public class GMMCallResult
{
	public enum Status
	{
		/**
		 * Indicates a generation as failed.
		 * It can be a compilation error, a project
		 * not found or other errors.
		 */
		FAILED,
		
		/**
		 * Indicates a generation succeed. A successful
		 * generation does not implies that
		 * no warning have been raised during generation.
		 */
		SUCCESS
	}
	
	protected java.util.Date						creationDate;
	protected Status								status;
	protected String								message;
	protected String								outputUrl;
	protected Map<String, List<String>>	errors;
	protected Map<String, List<String>>	warnings;
	
	/**
	 * Returns the report creation date.
	 * @return a {@link java.util.Date}.
	 */
	public java.util.Date getCreationDate()
	{
		return creationDate;
	}
	
	public void setCreationDate(java.util.Date creationDate)
	{
		this.creationDate = creationDate;
	}
	
	/**
	 * Returns the status of the generation.
	 * @return a status that indicates if the
	 *         generation succeed or failed.
	 * @see Status#SUCCESS
	 * @see Status#FAILED
	 */
	public Status getStatus()
	{
		return status;
	}
	
	public void setStatus(Status status)
	{
		this.status = status;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	public String getOutputUrl()
	{
		return outputUrl;
	}
	
	public void setOutputUrl(String outputUrl)
	{
		this.outputUrl = outputUrl;
	}
	
	public void setWarnings(Map<String, List<String>> warnings)
	{
		this.warnings = warnings;
	}
	
	public void setErrors(Map<String, List<String>> errors)
	{
		this.errors = errors;
	}
	
	public Map<String, List<String>> getWarnings()
	{
		return warnings;
	}
	
	public Map<String, List<String>> getErrors()
	{
		return errors;
	}
	
	public boolean hasErrors() {
		return getErrors() != null && !getErrors().isEmpty();
	}
	
	public boolean hasWarnings() {
		return getWarnings() != null && !getWarnings().isEmpty();
	}
}
