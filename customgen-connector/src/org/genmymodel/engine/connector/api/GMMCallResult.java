package org.genmymodel.engine.connector.api;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class represents a result obtained from an API call.
 * 
 * @author Vincent Aranega
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GMMCallResult {
	protected String outputUrl;
	protected Map<String, List<String>> errors;
	protected Map<String, List<String>> warnings;

	/**
	 * Gets the result output URL.
	 * @return the result URL.
	 */
	public String getOutputUrl() {
		return outputUrl;
	}

	/**
	 * Sets the result output URL.
	 * @param outputUrl The result URL.
	 */
	public void setOutputUrl(String outputUrl) {
		this.outputUrl = outputUrl;
	}

	/**
	 * Sets a map containing warnings that has
	 * occured during the API call.
	 * @param warnings the warning map.
	 */
	public void setWarnings(Map<String, List<String>> warnings) {
		this.warnings = warnings;
	}

	public void setErrors(Map<String, List<String>> errors) {
		this.errors = errors;
	}

	public Map<String, List<String>> getWarnings() {
		return warnings;
	}

	public Map<String, List<String>> getErrors() {
		return errors;
	}

	public boolean hasErrors() {
		return getErrors() != null && !getErrors().isEmpty();
	}

	public boolean hasWarnings() {
		return getWarnings() != null && !getWarnings().isEmpty();
	}
}
