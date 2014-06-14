/**
 * 
 */
package com.dave;

import java.awt.Button;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * @author dave
 *
 */
public class MainApplication extends JFrame  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1186753475176386282L;
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Document dbConnectivityStateModel = new PlainDocument();

		// Start database connectivity thread
		DbConnectivityThread dbConnectivityThread = new DbConnectivityThread(dbConnectivityStateModel);
		
		// Start the application
		JFrame application = new MainApplication();
		
		application.setLayout(new GridLayout(7,2));

		// Create the database name label and text field
        application.add(new JLabel("Database Name: "));
        JTextField dbNameTextField = new JTextField();
        dbConnectivityThread.setDbNameModel(dbNameTextField.getDocument());
        application.add(dbNameTextField);
        
        application.add(new JLabel("Collection Name: "));
        JTextField collectionNameTextField = new JTextField();
        dbConnectivityThread.setCollectionNameModel(collectionNameTextField.getDocument());
        application.add(collectionNameTextField);

        application.add(new JLabel("Field Name: "));
        JTextField fieldNameTextField = new JTextField();
        dbConnectivityThread.setFieldNameModel(fieldNameTextField.getDocument());
        application.add(fieldNameTextField);
        
        application.add(new JTextArea(dbConnectivityStateModel));
        application.add(new Button("6"));
		
		
		application.setVisible(true);
		
		dbConnectivityThread.start();

	}

	@Override
	public String getTitle() {
		return "MongoTwitter";
	}


}
