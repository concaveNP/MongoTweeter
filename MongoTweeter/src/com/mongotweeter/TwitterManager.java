package com.mongotweeter;

import java.io.IOException;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.eclipse.egit.github.core.Authorization;
import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.GistFile;
import org.eclipse.egit.github.core.service.GistService;

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
	 * The gist document's name prefix string
	 */
	private static final String GIST_DOC_PREFIX = "MongoTweeter_results_";

	/**
	 * The gist document's name postfix string
	 */
	private static final String GIST_DOC_POSTFIX = ".txt";

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
	private GistAuthentication myGistAuth;
	private Document myModel;
	private boolean myTweetingEnabled = false;
	private boolean myUserLoggedIn = false;
	private Twitter myTwitter;

	public TwitterManager(TwitterAuthentication twitterAuth,
			GistAuthentication gistAuth, Document model) {
		this.myTwitterAuth = twitterAuth;
		this.myGistAuth = gistAuth;
		this.myModel = model;
		this.myTwitter = new TwitterFactory().getInstance();
	}

	@Override
	public void run() {

		try {
			String result = createUniqueGistFilename();

			// Set authorization with 'gist' scope only
			Authorization auth = myGistAuth.getAuthorization();

			// Create Gist service configured with OAuth2 token
			GistService gistService = new GistService();
			gistService.getClient().setOAuth2Token(auth.getToken());

			// Create Gist
			Gist gist = new Gist();
			gist.setPublic(false);
			gist.setDescription("Created using OAuth2 token via Java API");
			GistFile file = new GistFile();
			file.setContent("Gist!");
			file.setFilename(createUniqueGistFilename());
			gist.setFiles(Collections.singletonMap(file.getFilename(), file));
			gist = gistService.createGist(gist);
			System.out.println("Created Gist at " + gist.getHtmlUrl());

			// Keep running until we are interrupted
			while (isInterrupted() == false) {

				// Check to see if there is a user checked in and tweeting is
				// enabled
				if ((myUserLoggedIn == true) && (isTweetingEnabled())) {

					try {
						String item = myQueue.poll(POLLING_FREQUENCY,
								TimeUnit.MILLISECONDS);

						// Check to see if there is something to process
						if (item != null) {
							
							// TODO - put in line number to reference DB document result in the gist file
							// Add the entry to the gist document
							
							// Check to see if the tweet is too long
							if (item.length() > MAX_TWEET_LENGTH) {

							} else {
								// Tweet the status update
								Status status = myTwitter.updateStatus(item);

								// Build the resulting URL and put it in the
								// data model being shown in the GUI
								String tweetUrl = buildTweetUrl(status
										.getUser().getScreenName(),
										status.getId());

								// Append the tweet the document model
								myModel.insertString(myModel.getLength(),
										tweetUrl + "\n", null);
							}

							// Sleep for a rule specified duration to keep
							// tweets from being too frequent
							sleep(myTwitterRules.getTweetFrequency());
						}
					} catch (TwitterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					// Nothing going on, lets sleep for a bit
					sleep(POLLING_FREQUENCY);
				}
			}
		} catch (InterruptedException e) {
			// This thread has been stopped
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public boolean isTweetingEnabled() {
		return myTweetingEnabled;
	}

	public void setTweetingEnabled(boolean value) {
		this.myTweetingEnabled = value;
	}

	/**
	 * NOTE: Had the future plan on this class being notified by an observable
	 * object.
	 */
	@Override
	public void update(Observable arg0, Object arg1) {

		if (arg0 instanceof TwitterAuthentication) {
			if (arg1 instanceof Boolean) {
				myUserLoggedIn = (Boolean) arg1;

				if (myUserLoggedIn == true) {
					// Set the new credentials
					myTwitter.setOAuthConsumer(TwitterAuthentication.API_KEY,
							TwitterAuthentication.API_KEY_SECRET);
					myTwitter.setOAuthAccessToken(myTwitterAuth
							.getAccessToken());
				}
			}
		} else if (arg1 instanceof String) {
			// Add the new tweet string to the queue of tweets waiting to be
			// processed
			if (arg1 instanceof String) {
				myQueue.add((String) arg1);
			}
		}
	}

	/**
	 * Builds the URL for the tweet that was made
	 * 
	 * @param userHandel
	 *            the user name
	 * @param tweetId
	 *            the unique twitter id given for this tweet
	 * 
	 * @return the result built URL
	 */
	private String buildTweetUrl(String userHandel, long tweetId) {
		return new String("https://twitter.com/" + userHandel + "/status/"
				+ Long.toString(tweetId));
	}

	/**
	 * Build a unique filename for out Gist interface which will be capturing
	 * this tweeting out document finds from the database. In order to create a
	 * unique filename a UUID is used within the filename.
	 * 
	 * @return a string containing the new filename
	 */
	private String createUniqueGistFilename() {
		StringBuilder result = new StringBuilder();

		result.append(GIST_DOC_PREFIX);
		result.append(UUID.randomUUID());
		result.append(GIST_DOC_POSTFIX);

		return result.toString();
	}

}
