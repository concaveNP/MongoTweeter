package com.dave;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * This is the where the tweets to Twitter are issued from. It is controlled via
 * its start and stop methods. Once started it will first verify that there is a
 * Twitter user who has granted access via Open Authentication for tweets to be
 * issued from this app.
 * 
 * It simply blocks upon a queue waiting for the new
 * 
 * @author dave
 */
public class TwitterManager extends Thread implements Observer {

	/**
	 * The duration the polling queue should wait for a new item to process
	 */
	private static long POLLING_FREQUENCY = 100;
	
	/**
	 * The maximum message size a tweet can be
	 */
	private static int MAX_TWEET_LENGTH = 140;
	
	private TwitterRules myTwitterRules = new TwitterRules();
	private BlockingQueue<String> myQueue = new LinkedBlockingQueue<String>();
	private TwitterAuthentication myTwitterAuth;
	private Document myModel;
	private boolean myTweetingEnabled = false;
	private boolean myUserLoggedIn = false;
	private Twitter myTwitter;
	
	public TwitterManager(TwitterAuthentication twitterAuth, Document model) {
		this.myTwitterAuth = twitterAuth;
		this.myModel = model;
		this.myTwitter = new TwitterFactory().getInstance();
	}
	
	@Override
	public void run() {

		try {
			// Keep running until we are interrupted
			while (isInterrupted() == false) {
				
				// Check to see if there is a user checked in and tweeting is enabled
				if ((myUserLoggedIn == true) && (isTweetingEnabled())) {
					
					try {
						String item = myQueue.poll(POLLING_FREQUENCY, TimeUnit.MILLISECONDS);

						// Check to see if there is something to process
						if (item != null)
						{
							// Check to see if the tweet is too long
							if (item.length() > MAX_TWEET_LENGTH) {
								// TODO - Get it to gist to work
							}
							else {
								// Tweet the status update
								Status status = myTwitter.updateStatus(item);
								
								// Build the resulting URL and put it in the data model being shown in the GUI 
								String tweetUrl = buildTweetUrl(status.getUser().getScreenName(), status.getId());
								
								// Append the tweet the document model
								myModel.insertString(myModel.getLength(), tweetUrl + "\n", null);
							}
							
							// Sleep for a rule specified duration to keep tweets from being too frequent
							sleep(myTwitterRules.getTweetFrequency());
						}
					} catch (TwitterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else {
					// Nothing going on, lets sleep for a bit
					sleep(POLLING_FREQUENCY);
				}
			}
		} catch (InterruptedException e) {
			// This thread has been stopped
		}
	}

	public boolean isTweetingEnabled() {
		return myTweetingEnabled;
	}

	public void setTweetingEnabled(boolean value) {
		this.myTweetingEnabled = value;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		
		if (arg0 instanceof TwitterAuthentication) {
			if (arg1 instanceof Boolean) {
				myUserLoggedIn = (Boolean)arg1;
				
				if (myUserLoggedIn == true) {
					// Set the new credentials
					myTwitter.setOAuthConsumer(TwitterAuthentication.API_KEY, TwitterAuthentication.API_KEY_SECRET);
					myTwitter.setOAuthAccessToken(myTwitterAuth.getAccessToken());
				}
			}
		}
		else if (arg0 == null) {
			// TODO - this will be checked for some tweet container observable 
			
			// Add the new tweet string to the queue of tweets waiting to be processed
			if (arg1 instanceof String) {
				myQueue.add((String) arg1);
			}
			
		}
		
	}
	
	private String buildTweetUrl(String userHandel, long tweetId) {
		return new String("https://twitter.com/" + userHandel + "/status/" + Long.toString(tweetId));
	}


}
