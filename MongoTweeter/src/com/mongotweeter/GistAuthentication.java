/**
 * 
 */
package com.mongotweeter;

import java.util.Arrays;

import org.eclipse.egit.github.core.Authorization;

/**
 * @author dave
 * 
 */
public class GistAuthentication {

	/**
	 * Set authorization with GIST_AUTHORIZATION_SCOPE_LEVEL scope only (See
	 * gist settings->applications for more info)
	 */
	private static final String GIST_AUTHORIZATION_SCOPE_LEVEL = "gist";

	/**
	 * The hard coded token for the developer user - for now 
	 */
	private static final String ACCESS_TOKEN_SECRET = "xxxxxxxxxxxxx";
	
	/**
	 * Build an Gist Authorization object which includes the access token for
	 * the user's GitHub Gist documents.
	 * 
	 * @return the authorization object
	 */
	public Authorization getAuthorization() {
		Authorization auth = new Authorization();

		auth.setScopes(Arrays.asList(GIST_AUTHORIZATION_SCOPE_LEVEL));
		auth.setToken(ACCESS_TOKEN_SECRET);

		return auth;
	}

}
