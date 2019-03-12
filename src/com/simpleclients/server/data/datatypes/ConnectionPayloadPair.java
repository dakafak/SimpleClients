package com.simpleclients.server.data.datatypes;

import com.simpleclients.connection.Connection;
import com.simpleclients.server.data.payload.Payload;

public class ConnectionPayloadPair {

	private Connection connection;
	private Payload payload;

	public ConnectionPayloadPair(Connection connection, Payload payload) {
		this.connection = connection;
		this.payload = payload;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Payload getPayload() {
		return payload;
	}

	public void setPayload(Payload payload) {
		this.payload = payload;
	}
}
