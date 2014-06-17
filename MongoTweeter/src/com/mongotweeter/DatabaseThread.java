/**
 * 
 */
package com.mongotweeter;

import java.net.UnknownHostException;
import java.util.StringTokenizer;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * @author dave
 * 
 */
public class DatabaseThread extends Thread {

	private Document myConnectivityStateModel;
	private Document myDbNameModel;
	private Document myCollectionNameModel;
	private Document myFieldNameAndFilterModel;
	private DBObject myFilter;
	private MongoClient myMongoClient;
	private MongoConnection myConnection;

	DatabaseThread(MongoConnection connection, Document document) {
		this.myConnection = connection;
		this.myConnectivityStateModel = document;
	}

	@Override
	public void run() {

		try {
			// TODO - more of the connection details could be applied here
			// Establish connection
			myMongoClient = new MongoClient(myConnection.getHost());

			// Continue running until we are interrupted
			while (!isInterrupted()) {

				// If everything is in place, show the user the number of
				// entries that could be published
				if (isRequirmentsMeet()) {
					countEntries();
				}

				// Don't overwhelm the thread, take a break
				sleep(1000);
			}
		} catch (InterruptedException e) {
			// This thread has been stopped
		} catch (UnknownHostException e) {
			// Update state text in GUI
			setConnectivtyStateText("Not Connected: The host is unknown");
		}
	}

	public void setDbNameModel(Document document) {
		this.myDbNameModel = document;
	}

	public void setCollectionNameModel(Document document) {
		this.myCollectionNameModel = document;
	}

	public void setFieldNameModel(Document document) {
		this.myFieldNameAndFilterModel = document;
	}

	/**
	 * @return the Filter that will be used to locate documents in the DB
	 */
	public DBObject getFilter() {
		return myFilter;
	}

	/**
	 * Checks to see if all the required information needed to request the
	 * information we want is available
	 * 
	 * @return True is we have all of the information we need and False
	 *         otherwise
	 */
	private boolean isRequirmentsMeet() {
		// Is there a DB name to work with
		if ((myDbNameModel == null) || (myDbNameModel.getLength() == 0)) {
			// Update state text in GUI
			setConnectivtyStateText("Not Connected: There is no DB name specified yet.");

			return false;
		}

		// Is there a collection name to work with
		if ((myCollectionNameModel == null)
				|| (myCollectionNameModel.getLength() == 0)) {
			// Update state text in GUI
			setConnectivtyStateText("Not Connected: There is no collection name specified yet.");

			return false;
		}

		// Is there a field name to work with
		if ((myFieldNameAndFilterModel == null)
				|| (myFieldNameAndFilterModel.getLength() == 0)) {
			// Update state text in GUI
			setConnectivtyStateText("Not Connected: There is no field name specified yet.");

			return false;
		} else {
			try {
				StringTokenizer tokens = new StringTokenizer(
						myFieldNameAndFilterModel.getText(0,
								myFieldNameAndFilterModel.getLength()), "><=",
						true);

				// Verify there is 3 or 4 tokens (user only gets to use one
				// relational operators)
				if (tokens.countTokens() == 3) {
					String key = tokens.nextToken().trim();
					RelationalOperator operator = RelationalOperator
							.fromHumanString(tokens.nextToken().trim());
					String thresholdString = tokens.nextToken().trim();
					Object threshold = thresholdString;

					try {
						Integer thresholdInteger = new Integer(thresholdString);
						threshold = thresholdInteger;
					} catch (NumberFormatException ex) {
						// do nothing
					}

					myFilter = new BasicDBObject(key, new BasicDBObject(
							operator.getMongo(), threshold));
				} else if (tokens.countTokens() == 4) {
					String key = tokens.nextToken().trim();
					RelationalOperator operator = RelationalOperator
							.fromHumanString(tokens.nextToken().trim()
									+ tokens.nextToken().trim());
					String thresholdString = tokens.nextToken().trim();
					Object threshold = thresholdString;

					try {
						Integer thresholdInteger = new Integer(thresholdString);
						threshold = thresholdInteger;
					} catch (NumberFormatException ex) {
						// do nothing
					}

					myFilter = new BasicDBObject(key, new BasicDBObject(
							operator.getMongo(), threshold));
				} else {
					// Update state text in GUI
					setConnectivtyStateText("Not Connected: Field name filter is badly formed");

					return false;
				}

			} catch (BadLocationException e) {
				// Update state text in GUI
				setConnectivtyStateText("Not Connected: Field name filter is badly formed");

				return false;
			} catch (NumberFormatException e) {
				// Update state text in GUI
				setConnectivtyStateText("Not Connected: Field name filter is badly formed");

				return false;
			}
		}

		// Success, all of the data appears to be present and well formed
		return true;
	}

	private void setConnectivtyStateText(String text) {
		try {
			// Clear the text
			myConnectivityStateModel.remove(0,
					myConnectivityStateModel.getLength());

			// Add the next text
			myConnectivityStateModel.insertString(0, text,
					SimpleAttributeSet.EMPTY);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void countEntries() {
		try {
			// Get the data
			DB db = myMongoClient.getDB(myDbNameModel.getText(0,
					myDbNameModel.getLength()).trim());
			DBCollection collection = db.getCollection(myCollectionNameModel
					.getText(0, myCollectionNameModel.getLength()).trim());
			DBCursor cursor = collection.find(myFilter);

			setConnectivtyStateText("Current Criteria yields: "
					+ cursor.count() + " documents");
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
