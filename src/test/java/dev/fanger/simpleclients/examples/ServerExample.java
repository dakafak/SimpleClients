package dev.fanger.simpleclients.examples;

import dev.fanger.simpleclients.examples.Tasks.ConnectionTask;
import dev.fanger.simpleclients.examples.Tasks.data.connection.User;
import dev.fanger.simpleclients.examples.Tasks.PingTask;
import dev.fanger.simpleclients.examples.Tasks.data.terminal.ActionRecord;
import dev.fanger.simpleclients.examples.Tasks.ActionTask;
import dev.fanger.simpleclients.logging.loggers.SystemPrintTimeLogger;
import dev.fanger.simpleclients.SimpleServer;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerExample {

	private SimpleServer simpleServer;

	public ServerExample() {
		ConcurrentHashMap<Integer, List<User>> sessionIdToUsers = new ConcurrentHashMap<>();
		ConcurrentHashMap<UUID, Integer> connectionIdToSessionId = new ConcurrentHashMap<>();

		ConcurrentLinkedQueue<ActionRecord> actionRecords = new ConcurrentLinkedQueue<>();
		simpleServer = new SimpleServer(1776);

		simpleServer.addTask(new ConnectionTask("/client/connect", sessionIdToUsers, connectionIdToSessionId));
		simpleServer.addTask(new PingTask("/test/ping"));
		simpleServer.addTask(new ActionTask("/test/action", actionRecords, sessionIdToUsers, connectionIdToSessionId));

		simpleServer.overrideLoggerType(SystemPrintTimeLogger.class);

		simpleServer.startListeningForConnections();
	}

	public SimpleServer getSimpleServer() {
		return simpleServer;
	}

}
