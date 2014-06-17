/**
 * 
 */
package com.mongotweeter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * @author dave
 * 
 */
public class MainApplication extends JFrame {

	/**
	 * Name of the application
	 */
	private static final String APPLICATION_NAME = "MongoTweeter";

	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = -1186753475176386282L;
	
	/**
	 * The minimum size of the application
	 */
	private static final Dimension myMinSize = new Dimension(700,300);

	private DatabaseThread myDatabaseThread;
	private JTextField myDbNameTextField;
	private JTextField myCollectionNameTextField;
	private JTextField myFieldNameTextField;
	private JButton myStartButton;
	private JButton myStopButton;
	private TwitterThread myTwitterThread;
	private TwitterAuthentication myTwitterAuth = new TwitterAuthentication();
	private GistAuthentication myGistAuth = new GistAuthentication();
	private Document myDbConnectivityStateModel = new PlainDocument();
	private Document myDbTweetsModel = new PlainDocument();
	private JButton myConnectButton;
	private JButton myDisconnectButton;
	private JTextField myConnectivityStateTextField;
	private DataPublisher myDataPublisher;
	private MongoConnection myDbConnection = new MongoConnection();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Start the application
		MainApplication application = new MainApplication();
		application.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		application.setSize(myMinSize);
		application.setMinimumSize(myMinSize);
		application.setLocation(700, 400); // TODO - get the screen dimension and use those values
		application.initialize();
		application.setVisible(true);
	}

	public void initialize() {

		// Create the various threads that make up the application
		myDatabaseThread = new DatabaseThread(myDbConnection,
				myDbConnectivityStateModel);
		myTwitterThread = new TwitterThread(myTwitterAuth, myGistAuth,
				myDbTweetsModel);

		setLayout(new BorderLayout());

		// Create DB panel
		createDbPanel();

		// Create Twitter panel
		createTwitterPanel();

		// Connect the twitter authorized observers
		myTwitterAuth.addObserver(myTwitterThread);

		// Start the threads
		myDatabaseThread.start();
		myTwitterThread.start();

		// Kick start the TwitterAuth into thinking a user has just got this app
		// authorized for use
		myTwitterAuth.setUserLoggedIn(true);
	}

	private void createDbPanel() {
		JPanel dbPanel = new JPanel();
		dbPanel.setBorder(BorderFactory.createTitledBorder("Database"));
		dbPanel.setLayout(new BorderLayout());

		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new BorderLayout());
		
		JPanel labelsPanel = new JPanel();
		labelsPanel.setLayout(new GridLayout(4,1));
		
		JPanel textsPanel = new JPanel();
		textsPanel.setLayout(new GridLayout(4,1));
		
		labelsPanel.add(new JLabel("Database Name: "));
		myDbNameTextField = new JTextField();
		myDatabaseThread.setDbNameModel(myDbNameTextField.getDocument());
		textsPanel.add(myDbNameTextField);

		labelsPanel.add(new JLabel("Collection Name: "));
		myCollectionNameTextField = new JTextField();
		myDatabaseThread.setCollectionNameModel(myCollectionNameTextField.getDocument());
		textsPanel.add(myCollectionNameTextField);

		labelsPanel.add(new JLabel("Field Name and Filter: "));
		myFieldNameTextField = new JTextField();
		myDatabaseThread.setFieldNameModel(myFieldNameTextField.getDocument());
		textsPanel.add(myFieldNameTextField);

		labelsPanel.add(new JLabel("Connection Status: "));
		myConnectivityStateTextField = new JTextField(myDbConnectivityStateModel, null, 0);
		myConnectivityStateTextField.setEditable(false);
		textsPanel.add(myConnectivityStateTextField);
		
		// Add the labels and texts panels to the search panel
		searchPanel.add(labelsPanel, BorderLayout.WEST);
		searchPanel.add(textsPanel, BorderLayout.CENTER);
		dbPanel.add(searchPanel, BorderLayout.NORTH);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		myConnectButton = new JButton("Start Data Publish");
		myConnectButton.setEnabled(true);
		myConnectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Change the enabled states on the DB widgets to reflect new DB
				// to twitter connectivity
				myConnectButton.setEnabled(false);
				myDbNameTextField.setEnabled(false);
				myCollectionNameTextField.setEnabled(false);
				myFieldNameTextField.setEnabled(false);
				myDisconnectButton.setEnabled(true);

				// Create a data publisher instance
				myDataPublisher = new DataPublisher(myDbConnection,
						myDbNameTextField.getText().trim(),
						myCollectionNameTextField.getText().trim(), myDatabaseThread
								.getFilter(), myTwitterThread);

				// Start the thread
				myDataPublisher.start();
			}
		});
		buttonPanel.add(myConnectButton);
		myDisconnectButton = new JButton("Stop Data Publish");
		myDisconnectButton.setEnabled(false);
		myDisconnectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Change the enabled states on the DB widgets to reflect new DB
				// to twitter connectivity
				myConnectButton.setEnabled(true);
				myDbNameTextField.setEnabled(true);
				myCollectionNameTextField.setEnabled(true);
				myFieldNameTextField.setEnabled(true);
				myDisconnectButton.setEnabled(false);

				// Kill the publisher thread
				stopThread(myDataPublisher);
			}
		});
		buttonPanel.add(myDisconnectButton);
		
		// Add buttons to DB panel
		dbPanel.add(buttonPanel, BorderLayout.SOUTH);

		add(dbPanel, BorderLayout.NORTH);
	}

	private void createTwitterPanel() {
		JPanel twitterPanel = new JPanel();
		twitterPanel.setBorder(BorderFactory.createTitledBorder("Twitter"));
		twitterPanel.setLayout(new BorderLayout());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		// The enable tweeting button
		myStartButton = new JButton("Enable Tweeting");
		myStartButton.setEnabled(true);
		myStartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Enable the tweeting
				myTwitterThread.setTweetingEnabled(true);

				// Disable the start button and enable the stop button now
				// the thread is running
				myStartButton.setEnabled(false);
				myStopButton.setEnabled(true);
			}
		});
		buttonPanel.add(myStartButton);
		
		// The disable tweeting button
		myStopButton = new JButton("Disable Tweeting");
		myStopButton.setEnabled(false);
		myStopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					// Clear the tweets
					myDbTweetsModel.remove(0, myDbTweetsModel.getLength());

					// Disable the tweeting
					myTwitterThread.setTweetingEnabled(false);

					// Enable the start button and disable the stop button now
					// the thread is stopped
					myStartButton.setEnabled(true);
					myStopButton.setEnabled(false);
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		buttonPanel.add(myStopButton);
		
		// Add buttons to twitter
		twitterPanel.add(buttonPanel, BorderLayout.SOUTH);

		JPanel tweetsPanel = new JPanel();
		tweetsPanel.setLayout(new BorderLayout());
		
		// Add the tweets
		tweetsPanel.add(new JLabel("Tweets: "), BorderLayout.NORTH);
		JTextArea tweetsTextArea = new JTextArea(myDbTweetsModel);
		tweetsTextArea.setBorder(BorderFactory
				.createLineBorder(getForeground()));
		tweetsTextArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(tweetsTextArea);
		tweetsPanel.add(scrollPane, BorderLayout.CENTER);
		
		// Add tweets to the twitter
		twitterPanel.add(tweetsPanel, BorderLayout.CENTER);

		add(twitterPanel, BorderLayout.CENTER);
	}

	@Override
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);

		if ((e.getID() == WindowEvent.WINDOW_CLOSING)
				|| (e.getID() == WindowEvent.WINDOW_CLOSED)) {
			// Stop the publisher thread
			stopThread(myDataPublisher);

			// Stop the database thread
			stopThread(myDatabaseThread);

			// Stop the twitter thread
			stopThread(myTwitterThread);
		}
	}

	@Override
	public String getTitle() {
		return APPLICATION_NAME;
	}

	/**
	 * Stop the specified thread
	 */
	private void stopThread(Thread thread) {
		try {
			if (thread != null) {
				thread.interrupt();
				thread.join();
				thread = null;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
