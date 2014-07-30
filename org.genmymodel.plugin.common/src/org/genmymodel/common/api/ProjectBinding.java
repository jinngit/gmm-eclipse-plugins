package org.genmymodel.common.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class provides a simple implementation that represents a GenMyModel
 * project abstraction from API.
 * 
 * @author Vincent Aranega
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectBinding {
	private String name;
	private String projectId;
	private boolean _public;
	
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
	 * @return the projectId
	 */
	public String getProjectId() {
		return projectId;
	}
	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(String proectId) {
		this.projectId = proectId;
	}
	/**
	 * @return the _public
	 */
	public boolean isPublic() {
		return _public;
	}
	/**
	 * @param _public the _public to set
	 */
	public void setPublic(boolean _public) {
		this._public = _public;
	}

}
