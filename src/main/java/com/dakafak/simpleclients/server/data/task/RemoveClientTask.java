package com.dakafak.simpleclients.server.data.task;

import com.dakafak.simpleclients.connection.Connection;
import com.dakafak.simpleclients.server.data.payload.Payload;
import com.dakafak.simpleclients.server.handlerthreads.datahelper.ConnectionReceiveDataHelper;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RemoveClientTask extends Task {

	private ConcurrentHashMap<UUID, Connection> clients;
	private ConcurrentHashMap<UUID, ConnectionReceiveDataHelper> connectionReceiveDataHelpers;

	public RemoveClientTask(ConcurrentHashMap<UUID, Connection> clients,
							ConcurrentHashMap<UUID, ConnectionReceiveDataHelper> connectionReceiveDataHelpers) {
		this.clients = clients;
		this.connectionReceiveDataHelpers = connectionReceiveDataHelpers;
	}

	@Override
	public void executeTask(Connection connection, Payload payload) {
		UUID connectionId = connection.getId();
		connection.closeConnection();
		System.out.println("Disconnected client: " + connection.getId());

		clients.remove(connectionId);
		connectionReceiveDataHelpers.remove(connectionId);
		System.out.println("Current client list size: " + clients.keySet().size());
	}
}
