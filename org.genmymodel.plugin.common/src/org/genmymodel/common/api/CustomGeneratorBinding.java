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
	private String generatorId;
	private String name;
	private String generatorURL;
	private String generatorBranch;
	private String generatorUser;
	private String generatorPass;
	private boolean _generatedOnGithub;
	
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
	 * @return the generatorBranch
	 */
	public String getGeneratorBranch() {
		return generatorBranch;
	}
	/**
	 * @param generatorBranch the generatorBranch to set
	 */
	public void setGeneratorBranch(String generatorBranch) {
		this.generatorBranch = generatorBranch;
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
	/**
	 * @return the generatorPass
	 */
	public String getGeneratorPass() {
		return generatorPass;
	}
	/**
	 * @param generatorPass the generatorPass to set
	 */
	public void setGeneratorPass(String generatorPass) {
		this.generatorPass = generatorPass;
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
}
