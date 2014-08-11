package org.genmymodel.common.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class provides a simple implementation that represents a GenMyModel
 * custom generator abstraction from API.
 * 
 * @author Ali Gourch
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomGeneratorBinding {
	private String name;
	private String generatorId;
	private String generatorURL;
	private String generatorUser;
	private boolean _generatedOnGithub;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the generatorId
	 */
	public String getGeneratorId() {
		return generatorId;
	}
	/**
	 * @param generatorId the generatorId to set
	 */
	public void setGeneratorId(String generatorId) {
		this.generatorId = generatorId;
	}
	/**
	 * @return the _generatedOnGithub
	 */
	public boolean isGeneratedOnGithub() {
		return _generatedOnGithub;
	}
	/**
	 * @param _generatedOnGithub the _generatedOnGithub to set
	 */
	public void setGeneratedOnGithub(boolean _generatedOnGithub) {
		this._generatedOnGithub = _generatedOnGithub;
	}
	/**
	 * @return the generatorURL
	 */
	public String getGeneratorURL() {
		return generatorURL;
	}
	/**
	 * @param generatorURL the generatorURL to set
	 */
	public void setGeneratorURL(String generatorURL) {
		this.generatorURL = generatorURL;
	}
	/**
	 * @return the generatorUser
	 */
	public String getGeneratorUser() {
		return generatorUser;
	}
	/**
	 * @param generatorUser the generatorUser to set
	 */
	public void setGeneratorUser(String generatorUser) {
		this.generatorUser = generatorUser;
	}
}
