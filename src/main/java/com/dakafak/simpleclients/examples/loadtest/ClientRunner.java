package com.dakafak.simpleclients.examples.loadtest;

import com.dakafak.simpleclients.connection.Connection;
import com.dakafak.simpleclients.examples.ClientExample;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientRunner implements Runnable {

	private Connection connection;
	private int sessionId;
	private ConcurrentLinkedQueue<ClientRunner> clientRunners;
	private ConcurrentLinkedQueue<ClientRunner> finishedClientRunners;
	private ClientExample clientExample;

	public ClientRunner(Connection connection,
						int sessionId,
						ConcurrentLinkedQueue<ClientRunner> clientRunners,
						ConcurrentLinkedQueue<ClientRunner> finishedClientRunners) {
		this.connection = connection;
		this.sessionId = sessionId;
		this.clientRunners = clientRunners;
		this.finishedClientRunners = finishedClientRunners;
	}

	@Override
	public void run() {
		clientExample = new ClientExample(connection, sessionId);
		connection.closeConnection();
		finishedClientRunners.add(this);
		clientRunners.remove(this);
	}

	public ClientExample getClientExample() {
		return clientExample;
	}

	public void setClientExample(ClientExample clientExample) {
		this.clientExample = clientExample;
	}
}
