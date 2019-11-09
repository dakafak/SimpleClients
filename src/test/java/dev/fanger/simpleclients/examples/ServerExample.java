package dev.fanger.simpleclients.examples;

import dev.fanger.simpleclients.examples.Tasks.ConnectionTask;
import dev.fanger.simpleclients.examples.Tasks.data.connection.User;
import dev.fanger.simpleclients.examples.Tasks.PingTask;
import dev.fanger.simpleclients.examples.Tasks.data.terminal.ActionRecord;
import dev.fanger.simpleclients.examples.Tasks.ActionTask;
import dev.fanger.simpleclients.logging.loggers.SystemPrintTimeLogger;
import dev.fanger.simpleclients.server.SimpleServer;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static dev.fanger.simpleclients.examples.Tasks.data.MyPayloadTypes.ACTION;
import static dev.fanger.simpleclients.examples.Tasks.data.MyPayloadTypes.CONNECTION_REQUEST;
import static dev.fanger.simpleclients.examples.Tasks.data.MyPayloadTypes.PING;

public class ServerExample {

	private SimpleServer simpleServer;

	public ServerExample() {
		ConcurrentHashMap<Integer, List<User>> sessionIdToUsers = new ConcurrentHashMap<>();
		ConcurrentHashMap<UUID, Integer> connectionIdToSessionId = new ConcurrentHashMap<>();

		ConcurrentLinkedQueue<ActionRecord> actionRecords = new ConcurrentLinkedQueue<>();
		simpleServer = new SimpleServer(1776);

		simpleServer.addTask(CONNECTION_REQUEST, new ConnectionTask(sessionIdToUsers, connectionIdToSessionId));
		simpleServer.addTask(PING, new PingTask());
		simpleServer.addTask(ACTION, new ActionTask(actionRecords, sessionIdToUsers, connectionIdToSessionId));

		simpleServer.overrideLoggerType(SystemPrintTimeLogger.class);

		simpleServer.startListeningForConnections();
	}

	public SimpleServer getSimpleServer() {
		return simpleServer;
	}

}
