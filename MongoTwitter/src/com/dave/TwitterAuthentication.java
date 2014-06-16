/**
 * 
 */
package com.dave;

import java.util.Observable;

import twitter4j.auth.AccessToken;

/**
 * This class is responsible for gathering the credentials needed for tweets to be issued from this application.  It listens in user input from the GUI and once all information is 
 * available the GUI  
 * 
 * 
 * @author dave
 *
 */
public class TwitterAuthentication extends Observable {


    
    private long myUserId = 1230508812;
    
    private Boolean myUserLoggedIn = new Boolean(false);
    
    /**
     * Build an access token using the stored token information
     * 
     * @return the access token
     */
	public AccessToken getAccessToken() {
		return new AccessToken(CONCAVENP_ACCESS_TOKEN, CONCAVENP_ACCESS_TOKEN_SECRET, myUserId);		
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
