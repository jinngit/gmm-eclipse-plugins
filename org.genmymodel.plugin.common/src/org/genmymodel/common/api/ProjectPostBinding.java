package org.genmymodel.common.api;

/**
 * @author Ali Gourch
 *
 */
public class ProjectPostBinding extends ProjectBinding {
	private byte[] base64data;

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
}
