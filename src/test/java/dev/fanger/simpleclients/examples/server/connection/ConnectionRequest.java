package dev.fanger.simpleclients.examples.server.connection;

import java.io.Serializable;

public class ConnectionRequest implements Serializable {

	private String name;
	private int sessionId;

	public ConnectionRequest(String name, int sessionId) {
		this.name = name;
		this.sessionId = sessionId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}
}
