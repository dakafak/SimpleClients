package dev.fanger.simpleclients.examples;

import dev.fanger.simpleclients.examples.connection.ConnectionTask;
import dev.fanger.simpleclients.examples.connection.User;
import dev.fanger.simpleclients.examples.ping.PingTask;
import dev.fanger.simpleclients.examples.terminal.ActionRecord;
import dev.fanger.simpleclients.examples.terminal.ActionTask;
import dev.fanger.simpleclients.server.SimpleServer;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static dev.fanger.simpleclients.examples.MyPayloadTypes.ACTION;
import static dev.fanger.simpleclients.examples.MyPayloadTypes.CONNECTION_REQUEST;

public class ServerExample {

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
