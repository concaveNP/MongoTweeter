package com.mongotweeter;

public class MongoConnection {

	private String myHost = "localhost";
	private int myPort;
	private String myUser;
	private String myPass;

	/**
	 * @return the Host
	 */
	public String getHost() {
		return myHost;
	}

	/**
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host) {
		this.myHost = host;
	}

	/**
	 * @return the port number
	 */
	public int getPort() {
		return myPort;
	}

	/**
	 * @param port
	 *            the port number to set
	 */
	public void setMyPort(int port) {
		this.myPort = port;
	}

	/**
	 * @return the database user name
	 */
	public String getUser() {
		return myUser;
	}

	/**
	 * @param user
	 *            the user name to set
	 */
	public void setMyUser(String user) {
		this.myUser = user;
	}

	/**
	 * @return the user password
	 */
	public String getPass() {
		return myPass;
	}

	/**
	 * @param pass
	 *            the user password to set
	 */
	public void setMyPass(String pass) {
		this.myPass = pass;
	}

}
