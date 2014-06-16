/**
 * 
 */
package com.dave;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

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
public class MainApplication extends JFrame {

	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = -1186753475176386282L;

	private DatabaseManager myDbManager;
	private JTextField myDbNameTextField;
	private JTextField myCollectionNameTextField;
	private JTextField myFieldNameTextField;
	private JButton myStartButton;
	private JButton myStopButton;
	private TwitterManager myTwitterManager;
	private TwitterAuthentication myTwitterAuth = new TwitterAuthentication();
	private Document myDbConnectivityStateModel = new PlainDocument();
	private Document myDbTweetsModel = new PlainDocument();
	private JButton myConnectButton;
	private JButton myDisconnectButton;
	private JTextArea myConnectivityStateTextArea;
	private DataPublisher myDataPublisher;
	private MongoConnection myDbConnection = new MongoConnection();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Start the application
		MainApplication application = new MainApplication();
		application.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		application.setSize(700, 300);
		application.setLocation(700, 400);
		application.initialize();
		application.setVisible(true);
	}

	public void initialize() {

		// Create the various threads that make up the application
		myDbManager = new DatabaseManager(myDbConnection,
				myDbConnectivityStateModel);
		myTwitterManager = new TwitterManager(myTwitterAuth, myDbTweetsModel);

		setLayout(new GridLayout(2, 1));

		// Create DB panel
		createDbPanel();

		// Create Twitter panel
		createTwitterPanel();

		// Connect the twitter authorized observers
		myTwitterAuth.addObserver(myTwitterManager);

		// Start the threads
		myDbManager.start();
		myTwitterManager.start();

		// Kick start the TwitterAuth into thinking a user has just got this app
		// authorized for use
		myTwitterAuth.setUserLoggedIn(true);
	}

	private void createDbPanel() {
		JPanel dbPanel = new JPanel();
		dbPanel.setBorder(BorderFactory.createTitledBorder("Database"));

		dbPanel.setLayout(new GridLayout(5, 2));

		// Create the database name label and text field
		dbPanel.add(new JLabel("Database Name: "));
		myDbNameTextField = new JTextField();
		myDbManager.setDbNameModel(myDbNameTextField.getDocument());
		// myDbNameTextField.setText("inventory");
		dbPanel.add(myDbNameTextField);

		dbPanel.add(new JLabel("Collection Name: "));
		myCollectionNameTextField = new JTextField();
		myDbManager.setCollectionNameModel(myCollectionNameTextField
				.getDocument());
		// myCollectionNameTextField.setText("publications");
		dbPanel.add(myCollectionNameTextField);

		dbPanel.add(new JLabel("Field Name and Filter: "));
		myFieldNameTextField = new JTextField();
		myDbManager.setFieldNameModel(myFieldNameTextField.getDocument());
		dbPanel.add(myFieldNameTextField);

		dbPanel.add(new JLabel("Connection Status: "));
		myConnectivityStateTextArea = new JTextArea(myDbConnectivityStateModel);
		myConnectivityStateTextArea.setEditable(false);
		myConnectivityStateTextArea.setBorder(BorderFactory
				.createLineBorder(getForeground()));
		dbPanel.add(myConnectivityStateTextArea);

		myConnectButton = new JButton("Connect DB to Twitter");
		myConnectButton.setEnabled(true);
		myConnectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Change the enabled states on the DB widgets to reflect new DB
				// to twitter connectivity
				myConnectButton.setEnabled(false);
				myDbNameTextField.setEnabled(false);
				myCollectionNameTextField.setEnabled(false);
				myFieldNameTextField.setEditable(false);
				myDisconnectButton.setEnabled(true);

				// Create a data publisher instance
				myDataPublisher = new DataPublisher(myDbConnection,
						myDbNameTextField.getText().trim(),
						myCollectionNameTextField.getText().trim(), myDbManager
								.getFilter(), myTwitterManager);

				// Start the thread
				myDataPublisher.start();
			}
		});
		dbPanel.add(myConnectButton);
		myDisconnectButton = new JButton("Disconnect DB from Twitter");
		myDisconnectButton.setEnabled(false);
		myDisconnectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Change the enabled states on the DB widgets to reflect new DB
				// to twitter connectivity
				myConnectButton.setEnabled(true);
				myDbNameTextField.setEnabled(true);
				myCollectionNameTextField.setEnabled(true);
				myFieldNameTextField.setEditable(true);
				myDisconnectButton.setEnabled(false);

				// Kill the publisher thread
				stopThread(myDataPublisher);
			}
		});
		dbPanel.add(myDisconnectButton);

		add(dbPanel);
	}

	private void createTwitterPanel() {
		JPanel twitterPanel = new JPanel();
		twitterPanel.setBorder(BorderFactory.createTitledBorder("Twitter"));

		twitterPanel.setLayout(new GridLayout(4, 2));

		myStartButton = new JButton("Start Tweeting");
		myStartButton.setEnabled(true);
		myStartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Enable the tweeting
				myTwitterManager.setTweetingEnabled(true);

				// Disable the start button and enable the stop button now
				// the thread is running
				myStartButton.setEnabled(false);
				myStopButton.setEnabled(true);
			}
		});
		twitterPanel.add(myStartButton);
		myStopButton = new JButton("Stop Tweeting");
		myStopButton.setEnabled(false);
		myStopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					// Clear the tweets
					myDbTweetsModel.remove(0, myDbTweetsModel.getLength());

					// Disable the tweeting
					myTwitterManager.setTweetingEnabled(false);

					// Enable the start button and disable the stop button now
					// the
					// thread is stopped
					myStartButton.setEnabled(true);
					myStopButton.setEnabled(false);
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		twitterPanel.add(myStopButton);

		twitterPanel.add(new JLabel("Tweets: "));
		JTextArea tweetsTextArea = new JTextArea(myDbTweetsModel);
		tweetsTextArea.setBorder(BorderFactory
				.createLineBorder(getForeground()));
		tweetsTextArea.setEditable(false);
		twitterPanel.add(tweetsTextArea);

		add(twitterPanel);
	}

	@Override
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);

		if ((e.getID() == WindowEvent.WINDOW_CLOSING)
				|| (e.getID() == WindowEvent.WINDOW_CLOSED)) {
			// Stop the publisher thread
			stopThread(myDataPublisher);

			// Stop the database thread
			stopThread(myDbManager);

			// Stop the twitter thread
			stopThread(myTwitterManager);
		}
	}

	@Override
	public String getTitle() {
		return "MongoTweetter";
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
