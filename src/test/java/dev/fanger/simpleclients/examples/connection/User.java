package dev.fanger.simpleclients.examples.connection;

import dev.fanger.simpleclients.connection.Connection;

public class User {

	private String name;
	private Connection connection;

	public User(String name, Connection connection) {
		this.name = name;
		this.connection = connection;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
}
