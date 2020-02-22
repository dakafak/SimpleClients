package dev.fanger.simpleclients.examples;

import dev.fanger.simpleclients.examples.server.tasks.BlastTask;
import dev.fanger.simpleclients.examples.server.tasks.ConnectionTask;
import dev.fanger.simpleclients.examples.server.connection.User;
import dev.fanger.simpleclients.examples.server.tasks.PingTask;
import dev.fanger.simpleclients.examples.server.data.ActionRecord;
import dev.fanger.simpleclients.examples.server.tasks.ActionTask;
import dev.fanger.simpleclients.examples.server.tasks.bounce.BounceTask1;
import dev.fanger.simpleclients.examples.server.tasks.bounce.BounceTask3;
import dev.fanger.simpleclients.exceptions.DuplicateTaskException;
import dev.fanger.simpleclients.logging.loggers.SystemPrintTimeLogger;
import dev.fanger.simpleclients.SimpleServer;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SimpleServerExample {

    private SimpleServer simpleServer;

    public SimpleServerExample() {
        ConcurrentHashMap<Integer, List<User>> sessionIdToUsers = new ConcurrentHashMap<>();
        ConcurrentHashMap<UUID, Integer> connectionIdToSessionId = new ConcurrentHashMap<>();
        ConcurrentLinkedQueue<ActionRecord> actionRecords = new ConcurrentLinkedQueue<>();

        try {
            simpleServer = new SimpleServer.Builder(1776)
                    .withLoggingType(SystemPrintTimeLogger.class)
                    .withTask("/client/connect", new ConnectionTask(sessionIdToUsers, connectionIdToSessionId))
                    .withTask("/test/ping", new PingTask())
                    .withTask("/test/action", new ActionTask(actionRecords, sessionIdToUsers, connectionIdToSessionId))
                    .withTask("/test/bounce/1", new BounceTask1())
                    .withTask("/test/bounce/3", new BounceTask3())
                    .withTask("/test/blast", new BlastTask())
                    .build();
        } catch (DuplicateTaskException e) {
            e.printStackTrace();
        }

        simpleServer.startListeningForConnections();
    }

    public void shutdownServer() {
        simpleServer.shutDownServer();
    }

    public SimpleServer getSimpleServer() {
        return simpleServer;
    }

}
