/**
 * 
 */
package com.dave;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * @author dave
 *
 */
public class MainApplication extends JFrame implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1186753475176386282L;
	
	private DatabaseManager myDbManager;
	
	private TwitterThread myTwitterThread;

	private JTextField myDbNameTextField;

	private JTextField myCollectionNameTextField;

	private JTextField myFieldNameTextField;

	private JButton myStartButton;
	private JButton myStopButton;

	private TwitterManager myTwitterManager;

	private TwitterAuthentication myTwitterAuth = new TwitterAuthentication();

	private Document myDbConnectivityStateModel = new PlainDocument();

	private Document myDbTweetsModel = new PlainDocument();
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Start the application
		MainApplication application = new MainApplication();
		application.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		application.setSize(600, 300);
		application.setLocation(700, 400);
		application.initialize();
		application.setVisible(true);
	}
	
	public void initialize() {
		
		// Create the various threads that make up the application
		myDbManager = new DatabaseManager(myDbConnectivityStateModel);
		myTwitterManager = new TwitterManager(myTwitterAuth , myDbTweetsModel);

		// Connect observers
		//myTwitterManager.getConnectedObservable()
		
		setLayout(new GridLayout(1,2));
		
		// Create DB panel
		createDbPanel();
		
		// Create Twitter panel
		createTwitterPanel();
		
		
		// Create the observers to connect the view
		myTwitterAuth.addObserver(this);
		myTwitterAuth.addObserver(myTwitterManager);


        // Start the threads
		myDbManager.start();
		myTwitterManager.start();
		
		// Kick start the TwitterAuth into thinking a user has just got this app authorized for use
		myTwitterAuth.setUserLoggedIn(true);
	}
	
	private void createDbPanel() {
		JPanel dbPanel = new JPanel();
		dbPanel.setBorder(BorderFactory.createTitledBorder("Database"));
		
		
		dbPanel.setLayout(new GridLayout(7,2));

		// Create the database name label and text field
		dbPanel.add(new JLabel("Database Name: "));
        myDbNameTextField = new JTextField();
        myDbManager.setDbNameModel(myDbNameTextField.getDocument());
        dbPanel.add(myDbNameTextField);
        
        dbPanel.add(new JLabel("Collection Name: "));
        myCollectionNameTextField = new JTextField();
        myDbManager.setCollectionNameModel(myCollectionNameTextField.getDocument());
        dbPanel.add(myCollectionNameTextField);

        dbPanel.add(new JLabel("Field Name and Filter: "));
        myFieldNameTextField = new JTextField();
        myDbManager.setFieldNameModel(myFieldNameTextField.getDocument());
        dbPanel.add(myFieldNameTextField);
        
        dbPanel.add(new JLabel("Connection Status: "));
        dbPanel.add(new JTextArea(myDbConnectivityStateModel));
		

		
		add(dbPanel);
	}
	
	private void createTwitterPanel() {
		JPanel twitterPanel = new JPanel();
		twitterPanel.setBorder(BorderFactory.createTitledBorder("Twitter"));
		
		twitterPanel.setLayout(new GridLayout(4,2));
		
		
        myStartButton = new JButton("Start Tweeting");
        myStartButton.setEnabled(true);
        myStartButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					// Clear the tweets document
					myDbTweetsModel.remove(0, myDbTweetsModel.getLength());

					// Enable the tweeting
					myTwitterManager.setTweetingEnabled(true);

					// Disable the start button and enable the stop button now the thread is running
					myStartButton.setEnabled(false);
					myStopButton.setEnabled(true);
					
//					myTwitterThread = new TwitterThread(myDbNameTextField.getText(), myCollectionNameTextField.getText(), myDbManager.getFilter());
//					myTwitterThread.start();
					
					myTwitterManager.update(null,"test dave");
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		});
        twitterPanel.add(myStartButton);
        myStopButton = new JButton("Stop Tweeting");
        myStopButton.setEnabled(false);
        myStopButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Disable the tweeting
				myTwitterManager.setTweetingEnabled(true);
				
				// Enable the start button and disable the stop button now the thread is stopped
				myStartButton.setEnabled(true);
				myStopButton.setEnabled(false);
			}
		});
        twitterPanel.add(myStopButton);
		
        twitterPanel.add(new JLabel("Tweets: "));
        JTextArea tweetsTextArea = new JTextArea(myDbTweetsModel);
        tweetsTextArea.setEditable(false);
        twitterPanel.add(tweetsTextArea);
		
		
		add(twitterPanel);
	}

	@Override
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		
		if ( (e.getID() == WindowEvent.WINDOW_CLOSING) || (e.getID() == WindowEvent.WINDOW_CLOSED) ) {
			// Stop the database thread
			stopDatabaseManager();
			
			// Stop the twitter thread
			stopTwitterManager();
		}
	}

	@Override
	public String getTitle() {
		return "MongoTwitter";
	}

	/**
	 * Stop the database thread
	 */
	private void stopDatabaseManager() {
		try {
			if (myDbManager != null) {
				myDbManager.interrupt();
				myDbManager.join();
				myDbManager = null;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * Stop the twitter thread
	 */
	private void stopTwitterManager() {
		try {
			if (myTwitterManager != null) {
				myTwitterManager.interrupt();
				myTwitterManager.join();
				myTwitterManager = null;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
//		if (arg0 instanceof TwitterAuthentication) {
//			if (arg1 instanceof Boolean) {
//				// The user just logged out or in
//				if (((Boolean)arg1) == true) {
//					// User logged in, enable the start button
//					myStartButton.setEnabled(true);
//					myStartButton.set
//					myStopButton.setEnabled(false);
//				}
//				else {
//					
//				}
//			}
//			
//	        myStartButton.setEnabled(false);
//		}
	}


}
