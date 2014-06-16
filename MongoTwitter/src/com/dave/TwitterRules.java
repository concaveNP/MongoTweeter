/**
 * 
 */
package com.dave;

/**
 * Container for the Twitter rules this application must follow.
 * 
 * @author dave
 */
public class TwitterRules {

	/**
	 * The fast you can get is a half second
	 */
	private static final int MIN_TWEET_FREQEUNCY = 500;

	/**
	 * Default to a frequency of 2 seconds
	 */
	private static final int DEFAULT_TWEET_FREQEUNCY = 2000;

	/**
	 * Frequency in milliseconds that tweets should be published out on
	 */
	private int myTweetFrequency = DEFAULT_TWEET_FREQEUNCY;

	/**
	 * Constructor
	 */
	public TwitterRules() {
		// do nothing
	}

	/**
	 * Getter for the frequency
	 * 
	 * @return the frequency of the tweets
	 */
	public int getTweetFrequency() {
		return myTweetFrequency;
	}

	/**
	 * Getter for the frequency
	 * 
	 * @return the frequency of the tweets
	 */
	public void setTweetFrequency(int frequency) {
		// Check that it is not too fast
		if (frequency >= MIN_TWEET_FREQEUNCY) {
			this.myTweetFrequency = frequency;
		} else {
			// TODO - log message
		}
	}

}
