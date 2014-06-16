/**
 * 
 */
package com.dave;

import java.net.UnknownHostException;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;


/**
 * @author dave
 *
 */
public class TwitterThread extends Thread {

	private static final String HOST = "localhost";
	
    private final static String CONSUMER_KEY = "6NilMcMIqBsoqg9xmIaooiWDP";
    private final static String CONSUMER_KEY_SECRET = "cZWva7fbADyrXsWbl8fBSW7wFDHJqDBau48grnEl9ltP1lw3wZ";
    
    private final static String CONCAVENP_ACCESS_TOKEN = "1230508812-2yJZ0k8kdKpEj1gAm73v0x9mO41QcoxJsD5JfCV";
    private final static String CONCAVENP_ACCESS_TOKEN_SECRET = "IvM5eKdhFdpreUZW65prHO7s3lVF2rfsTAQY5T0EGAbL9";
	
	private String myDb;
	private String myCollection;
	private DBObject myFilter;
	
	private boolean myUserLoggedIn = false;

	public TwitterThread(String db, String collection, DBObject filter) {
		this.myDb = db;
		this.myCollection = collection;
		this.myFilter = filter;
	}
	
	/**
	 * Boolean check to see if a user has already gone through and authorized this app to perform tweeting on their behalf
	 *  
	 * @return true if the has logged in and authorized this app and false otherwise 
	 */
	private boolean isUserLoggedIn() {
		return myUserLoggedIn;
	}
	
	@Override
	public void run() {

		MongoClient mongoClient;

		
		
		try {
			
//			// Check to see if there is twitter user logged in through this app
//			if (isUserLoggedIn() == false) {
//				// We will need to ask the user to log in
//			}
				
			
			// Set twitter connection
			Twitter twitter = new TwitterFactory().getInstance();

			twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);
			twitter.setOAuthAccessToken(new AccessToken(CONCAVENP_ACCESS_TOKEN,
					CONCAVENP_ACCESS_TOKEN_SECRET, 1230508812));

			Status status = twitter.updateStatus("hi.. im updating my status using my app to demo tweet functionality");
			String userHandel = status.getUser().getScreenName();
			long tweetId = status.getId();
			String tweetUrl = buildTweetUrl(userHandel, tweetId);
			
			System.out.println(tweetUrl);
			
			
			
//			// Establish connection
//			mongoClient = new MongoClient( HOST );
//			
//			DB db = mongoClient.getDB( myDb );
//			DBCollection collection = db.getCollection(myCollection);
//			DBCursor cursor = collection.find(myFilter);
			
			
			
			
//			while (cursor.hasNext()) {
//				sleep(1000);
//				
//				System.out.println(cursor.next());
//			}			
			
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}

	private String buildTweetUrl(String userHandel, long tweetId) {
		return new String("https://twitter.com/" + userHandel + "/status/" + Long.toString(tweetId));
	}

	
	
}
