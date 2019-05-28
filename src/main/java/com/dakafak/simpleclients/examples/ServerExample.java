package com.dakafak.simpleclients.examples;

import com.dakafak.simpleclients.examples.connection.ConnectionTask;
import com.dakafak.simpleclients.examples.connection.User;
import com.dakafak.simpleclients.examples.ping.PingTask;
import com.dakafak.simpleclients.examples.terminal.ActionRecord;
import com.dakafak.simpleclients.examples.terminal.ActionTask;
import com.dakafak.simpleclients.server.SimpleServer;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.dakafak.simpleclients.examples.MyPayloadTypes.ACTION;
import static com.dakafak.simpleclients.examples.MyPayloadTypes.CONNECTION_REQUEST;

public class ServerExample {

	public static void main(String[] args) {
		new ServerExample();
	}

	public ServerExample() {
		ConcurrentHashMap<Integer, List<User>> sessionIdToUsers = new ConcurrentHashMap<>();
		ConcurrentHashMap<UUID, Integer> connectionIdToSessionId = new ConcurrentHashMap<>();

		ConcurrentLinkedQueue<ActionRecord> actionRecords = new ConcurrentLinkedQueue<>();
		SimpleServer simpleServer = new SimpleServer(1776);

		simpleServer.addTask(CONNECTION_REQUEST, new ConnectionTask(sessionIdToUsers, connectionIdToSessionId));
		simpleServer.addTask(MyPayloadTypes.PING, new PingTask());
		simpleServer.addTask(ACTION, new ActionTask(actionRecords, sessionIdToUsers, connectionIdToSessionId));

		simpleServer.startListeningForConnections();
	}

}
