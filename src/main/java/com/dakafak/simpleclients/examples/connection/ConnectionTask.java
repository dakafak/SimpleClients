package com.dakafak.simpleclients.examples.connection;

import com.dakafak.simpleclients.connection.Connection;
import com.dakafak.simpleclients.connection.Id;
import com.dakafak.simpleclients.server.data.payload.Payload;
import com.dakafak.simpleclients.server.data.task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionTask extends Task {

	private ConcurrentHashMap<Integer, List<User>> sessionIdToUsers;
	private ConcurrentHashMap<Id, Integer> connectionIdToSessionId;

	public ConnectionTask(ConcurrentHashMap<Integer, List<User>> sessionIdToUsers,
						  ConcurrentHashMap<Id, Integer> connectionIdToSessionId) {
		this.sessionIdToUsers = sessionIdToUsers;
		this.connectionIdToSessionId = connectionIdToSessionId;
	}

	@Override
	public void executeTask(Connection connection, Payload payload) {
		if(payload.getData() instanceof ConnectionRequest) {
			// Lol who even uses security?
			ConnectionRequest connectionRequest = (ConnectionRequest) payload.getData();
			User newUser = new User(connectionRequest.getName(), connection);

			System.out.println("Added new client: \"" + newUser.getName() + "\" to session " + connectionRequest.getSessionId());

			if(sessionIdToUsers.containsKey(connectionRequest.getSessionId())) {
				sessionIdToUsers.get(connectionRequest.getSessionId()).add(newUser);
			} else {
				List<User> usersList = new ArrayList<>();
				usersList.add(newUser);
				sessionIdToUsers.put(connectionRequest.getSessionId(), usersList);
			}
			connectionIdToSessionId.put(connection.getId(), connectionRequest.getSessionId());
		}
	}

}
