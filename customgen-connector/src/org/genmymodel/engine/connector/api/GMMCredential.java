package org.genmymodel.engine.connector.api;

/**
 * This class represents a simple credential implementation.
 * 
 * @author Vincent Aranega
 *
 */
public class GMMCredential {

	private String username;
	private String password;

	/**
	 * Creates a new credential.
	 * @param username the user name.
	 * @param password the user password.
	 */
	public GMMCredential(String username, String password) {
		setUsername(username);
		setPassword(password);
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

}
