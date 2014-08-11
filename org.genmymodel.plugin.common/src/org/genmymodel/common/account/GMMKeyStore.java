package org.genmymodel.common.account;

import java.util.HashMap;
import java.util.Map;

/**
 *  This class manage the keyStore.
 * @author Ali Gourch
 */
public class GMMKeyStore {

	public static Map<String, GMMCredential> CREDENTIALS;
	public static GMMCredential CREDENTIAL;
	public static GMMKeyStore KEYSTORE = new GMMKeyStore();
	
	/**
	 * Prevents external instance creation.
	 */
	private GMMKeyStore() {
		initialize();
	}
	
	synchronized protected void initialize() {
		GMMKeyStore.CREDENTIALS = new HashMap<String, GMMCredential>();
	}
	
	/**
	 * Gets the GMMKeyStore instance.
	 * @return The GMMKeyStore instance.
	 */
	public static GMMKeyStore getInstance() {
		return KEYSTORE;
	}
	
	/**
	 * gets the GMMKeyStore credentials.
	 * @return The map of credentials.
	 */
	synchronized public Map<String, GMMCredential> getCredentials() {
		return GMMKeyStore.CREDENTIALS;
	}
	
	/**
	 * Adds the GMMKeyStore credential.
	 */
	synchronized public void addCredential(String username, GMMCredential credential) {
		GMMKeyStore.CREDENTIALS.put(username, credential);
	}
	
	/**
	 * Removes the GMMKeyStore credential.
	 */
	synchronized public void removeCredential(String username) {
		GMMKeyStore.CREDENTIALS.remove(username);
	}
	
	/**
	 * gets the GMMKeyStore credential.
	 * @return The current credential.
	 */
	synchronized public GMMCredential getCurrentCredential() {
		return GMMKeyStore.CREDENTIAL;
	}
	
	/**
	 * Loads the GMMKeyStore credential.
	 */
	synchronized public void loadCredential(String username) {
		GMMKeyStore.CREDENTIAL = GMMKeyStore.CREDENTIALS.get(username);	
	}
}
