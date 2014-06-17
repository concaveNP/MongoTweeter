/**
 * 
 */
package com.mongotweeter;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * @author dave
 * 
 */
public class DataPublisher extends Thread {

	private MongoConnection myConnection;
	private String myDb;
	private String myCollection;
	private DBObject myFilter;
	private TwitterThread myTwitterThread;

	public DataPublisher(MongoConnection connection, String db,
			String collection, DBObject filter, TwitterThread twitterThread) {
		this.myConnection = connection;
		this.myDb = db;
		this.myCollection = collection;
		this.myFilter = filter;
		this.myTwitterThread = twitterThread;
	}

	@Override
	public void run() {
		MongoClient mongoClient = null;

		// First verify there is stuff to work with - this is hiding the bug
		// that this thread should not start unless the data is good to go
		if ((myDb == null) || (myCollection == null) || (myFilter == null)) {
			return;
		}

		try {
			// TODO - more of the connection details could be applied here
			// Establish connection
			mongoClient = new MongoClient(myConnection.getHost());

			// Get the DB data
			DB db = mongoClient.getDB(myDb);
			DBCollection collection = db.getCollection(myCollection);
			DBCursor cursor = collection.find(myFilter);

			// Publish data
			while ((!isInterrupted()) && (cursor.hasNext())) {
				String document = cursor.next().toString();
				myTwitterThread.update(null, document);
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// close db connection
			if (mongoClient != null) {
				mongoClient.close();
			}
		}
	}
}
