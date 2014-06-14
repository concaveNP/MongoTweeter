/**
 * 
 */
package com.dave;

import java.net.UnknownHostException;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;

import com.mongodb.MongoClient;

/**
 * @author dave
 *
 */
public class DbConnectivityThread extends Thread {

	private static final String HOST = "localhost";
	private MongoClient myMongoClient;
	private Document myConnectivityStateModel;
	private Document myDbNameModel;
	private Document myCollectionNameModel;
	private Document myFieldNameModel;
	
	DbConnectivityThread(Document document) {
		this.myConnectivityStateModel = document;
	}
	
	@Override
	public void run() {
		
		// Continue running until we are interrupted
		while ( !isInterrupted() ) {
			
			try {
				if ( isRequirmentsMeet() ) {
	
					// Connect to DB
					ConnectToDb();
					
					// Test connection
					
					// Update text in GUI
					setConnectivtyStateText("Connected!");
				}
			} catch (UnknownHostException ex) {
				ex.printStackTrace();
				
				// Update text in GUI
				setConnectivtyStateText("Not Connect: Unable to connect to the specified host \"" + HOST + "\" due to: " + ex.getMessage() );
			}
			

			// Don't overwhelm the thread, take a break
			try {
				sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void ConnectToDb() throws UnknownHostException {
		myMongoClient = new MongoClient( HOST );
		
		//DB db;
		//db = myMongoClient.getDB( myDbNameModel.getText(0, myDbNameModel.getLength()) );
	}

	/**
	 * Checks to see if all the required information needed to request the information we want is available
	 * 
	 * @return True is we have all of the information we need and False otherwise
	 */
	private boolean isRequirmentsMeet() {
		// Is there a DB name to work with
		if ( (myDbNameModel == null) || (myDbNameModel.getLength() == 0) ) {
			// Update state text in GUI
			setConnectivtyStateText("Not Connected: There is no DB name specified yet.");
			
			return false;
		}

		// Is there a collection name to work with
		if ( (myCollectionNameModel == null) || (myCollectionNameModel.getLength() == 0) ) {
			// Update state text in GUI
			setConnectivtyStateText("Not Connected: There is no collection name specified yet.");
			
			return false;
		}
		
		// Is there a field name to work with
		if ( (myFieldNameModel == null) || (myFieldNameModel.getLength() == 0) ) {
			// Update state text in GUI
			setConnectivtyStateText("Not Connected: There is no field name specified yet.");
			
			return false;
		}

		// Success, all of the data appears to be present
		return true;
	}

	private void setConnectivtyStateText(String text) {
		try {
			// Clear the text
			myConnectivityStateModel.remove(0, myConnectivityStateModel.getLength());
			
			// Add the next text
			myConnectivityStateModel.insertString(0, text, SimpleAttributeSet.EMPTY);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setDbNameModel(Document document) {
		this.myDbNameModel = document;
	}

	public void setCollectionNameModel(Document document) {
		this.myCollectionNameModel = document;
	}

	public void setFieldNameModel(Document document) {
		this.myFieldNameModel = document;
	}

}
