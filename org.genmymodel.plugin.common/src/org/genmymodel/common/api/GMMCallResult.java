package org.genmymodel.common.api;

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
	private static final String OUTPUT_URL = "output";
	protected List<Href> links;
	protected Map<String, List<String>> errors;
	protected Map<String, List<String>> warnings;

	/**
	 * Required empty constructor
	 */
	public GMMCallResult() {
	}

	/**
	 * @return the links
	 */
	public List<Href> getLinks() {
		return links;
	}

	/**
	 * @param links
	 *            the links to set
	 */
	public void setLinks(List<Href> links) {
		this.links = links;
	}

	/**
	 * Sets a map containing warnings that has occured during the API call.
	 * 
	 * @param warnings
	 *            the warning map.
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
	
	public String getOutputUrl() {
		for (Href href : links) {
			if (OUTPUT_URL.equals(href.getRel())) {
				return href.getHref();
			}
		}
		
		return null;
	}

	public static class Href {
		private String rel;
		private String href;

		public String getRel() {
			return rel;
		}

		public void setRel(String rel) {
			this.rel = rel;
		}

		public String getHref() {
			return href;
		}

		public void setHref(String href) {
			this.href = href;
		}
	}
}
