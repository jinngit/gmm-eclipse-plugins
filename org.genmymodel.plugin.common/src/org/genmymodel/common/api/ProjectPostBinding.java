package org.genmymodel.common.api;

/**
 * 
 * @author Vincent Aranega
 *
 */
public class ProjectPostBinding extends ProjectBinding {

	private byte[] base64data;
	private String defaultDiagram;
	
	/**
	 * @return the data
	 */
	public byte[] getData() {
		return base64data;
	}
	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(byte[] base64data) {
		this.base64data = base64data;
	}
	
	/**
	 * @return the default diagram type to create in this project
	 */
	public String getDefaultDiagram() {
		return defaultDiagram;
	}

	/**
	 * @param defaultDiagram the default diagram type to create in this project
	 */
	public void setDefaultDiagram(String defaultDiagram) {
		this.defaultDiagram = defaultDiagram;
	}
}
