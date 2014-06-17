/**
 * 
 */
package com.mongotweeter;

import java.util.Observable;

import twitter4j.auth.AccessToken;

/**
 * This class is responsible for gathering the credentials needed for tweets to
 * be issued from this application. It listens in user input from the GUI and
 * once all information is available the GUI
 * 
 * 
 * @author dave
 * 
 */
public class TwitterAuthentication extends Observable {

	/**
	 * This application's key used for OAuth
	 */
	public static final String API_KEY = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
	
	/**
	 * This application's secret key used for OAuth
	 */
	public static final String API_KEY_SECRET = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

	/**
	 * The hard coded token for the developer user - for now 
	 */
	private static final String CONCAVENP_ACCESS_TOKEN = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

	/**
	 * The hard coded secret token for the developer user - for now 
	 */
	private static final String CONCAVENP_ACCESS_TOKEN_SECRET = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

	/**
	 * The hard coded developer user ID - for now 
	 */
	private static long myUserId = 00000000000000000000000000000;

	/**
	 * The state of the this application being logged in as seen from the user's twitter account
	 */
	private Boolean myUserLoggedIn = new Boolean(false);

	/**
	 * Build an access token using the stored token information
	 * 
	 * @return the access token
	 */
	public AccessToken getAccessToken() {
		return new AccessToken(CONCAVENP_ACCESS_TOKEN,
				CONCAVENP_ACCESS_TOKEN_SECRET, myUserId);
	}

	/**
	 * Setter for the state of a user being logged in and approved of using the
	 * application. Calling this method triggers notifying the observers of this
	 * change.
	 * 
	 * @param value
	 *            true of the user is now logged in and false otherwise
	 */
	public void setUserLoggedIn(boolean value) {
		myUserLoggedIn = new Boolean(value);

		setChanged();
		notifyObservers(myUserLoggedIn);
	}

}
