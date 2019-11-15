package dev.fanger.simpleclients.examples;

import dev.fanger.simpleclients.examples.server.tasks.ConnectionTask;
import dev.fanger.simpleclients.examples.server.connection.User;
import dev.fanger.simpleclients.examples.server.tasks.PingTask;
import dev.fanger.simpleclients.examples.server.data.ActionRecord;
import dev.fanger.simpleclients.examples.server.tasks.ActionTask;
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

		simpleServer = new SimpleServer.Builder(1776)
				.withLoggingType(SystemPrintTimeLogger.class)
				.withTask(new ConnectionTask("/client/connect", sessionIdToUsers, connectionIdToSessionId))
				.withTask(new PingTask("/test/ping"))
				.withTask(new ActionTask("/test/action", actionRecords, sessionIdToUsers, connectionIdToSessionId))
				.build();

		simpleServer.startListeningForConnections();
	}

	public void shutdownServer() {
		simpleServer.shutDownServer();
	}

}
