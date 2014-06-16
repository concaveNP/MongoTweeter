/**
 * 
 */
package com.dave;

import java.util.StringTokenizer;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * @author dave
 *
 */
public class DatabaseManager extends Thread {

	private Document myConnectivityStateModel;
	private Document myDbNameModel;
	private Document myCollectionNameModel;
	private Document myFieldNameAndFilterModel;
	private DBObject myFilter;
	
	DatabaseManager(Document document) {
		this.myConnectivityStateModel = document;
	}
	
	@Override
	public void run() {

		try {
			// Continue running until we are interrupted
			while ( !isInterrupted() ) {
				
				if ( isRequirmentsMeet() ) {
				    // Have everything I need, inform the GUI
					setConnectivtyStateText("looking good!");
				}
	
				// Don't overwhelm the thread, take a break
				sleep(1000);
			}
		} catch (InterruptedException e) {
			// This thread has been stopped
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
		if ( (myFieldNameAndFilterModel == null) || (myFieldNameAndFilterModel.getLength() == 0) ) {
			// Update state text in GUI
			setConnectivtyStateText("Not Connected: There is no field name specified yet.");
			
			return false;
		}
		else {
			try {
				StringTokenizer tokens = new StringTokenizer(myFieldNameAndFilterModel.getText(0, myFieldNameAndFilterModel.getLength()), "><=", true);
				
				// Verify there is 3 or 4 tokens (user only gets to use one relational operators)
				if (tokens.countTokens() == 3) {
					String key = tokens.nextToken();
					RelationalOperator operator = RelationalOperator.fromHumanString(tokens.nextToken());
					String threshold = tokens.nextToken(); 
					
					myFilter = new BasicDBObject(key, new BasicDBObject(operator.getMongo(), new Integer(threshold)));
				}
				else if (tokens.countTokens() == 4) {
					String key = tokens.nextToken();
					RelationalOperator operator = RelationalOperator.fromHumanString(tokens.nextToken() + tokens.nextToken());
					String threshold = tokens.nextToken(); 
					
					myFilter = new BasicDBObject(key, new BasicDBObject(operator.getMongo(), new Integer(threshold)));
				}
				else {
					// The is filter is badly formed

					// Update state text in GUI
					setConnectivtyStateText("Not Connected: There is no field name filter is badly formed");
					
					return false;
				}
				
			} catch (BadLocationException e) {
				// Update state text in GUI
				setConnectivtyStateText("Not Connected: There is no field name filter is badly formed");
				
				return false;
			} catch (NumberFormatException e) {
				// Update state text in GUI
				setConnectivtyStateText("Not Connected: There is no field name filter is badly formed");
				
				return false;
			}
		}

		// Success, all of the data appears to be present and well formed
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
	


}
