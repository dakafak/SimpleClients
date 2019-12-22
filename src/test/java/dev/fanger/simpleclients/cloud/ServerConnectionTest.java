package dev.fanger.simpleclients.cloud;

import dev.fanger.simpleclients.SimpleServer;
import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.examples.server.tasks.ExpensiveTask;
import dev.fanger.simpleclients.examples.server.tasks.PingTask;
import dev.fanger.simpleclients.exceptions.DuplicateTaskException;
import dev.fanger.simpleclients.logging.loggers.SystemPrintTimeLogger;
import dev.fanger.simpleclients.server.ServerConnectionInfo;
import dev.fanger.simpleclients.server.data.payload.Payload;

import java.util.ArrayList;
import java.util.List;

public class ServerConnectionTest {

    public static void main(String[] args) {
        new ServerConnectionTest();
    }

    public ServerConnectionTest() {
        try {
            SimpleServer primaryServer = new SimpleServer.Builder(999)
                    .withLoggingType(SystemPrintTimeLogger.class)
                    .withTask("/test/ping", new PingTask())
                    .withTask("/test/expensive", new ExpensiveTask(999))
                    .withCloudConnectionInfo(new ServerConnectionInfo("127.0.0.1", 1001))
                    .build();

            SimpleServer secondaryServer = new SimpleServer.Builder(1001)
                    .withLoggingType(SystemPrintTimeLogger.class)
                    .withTask("/test/ping", new PingTask())
                    .withTask("/test/expensive", new ExpensiveTask(1001))
                    .withCloudConnectionInfo(new ServerConnectionInfo("127.0.0.1", 999))
                    .build();

            primaryServer.startListeningForConnections();
            secondaryServer.startListeningForConnections();

            List<Connection> allConnections = new ArrayList<>();

            for(int i = 0; i < 10; i++) {
                Connection newConnection = Connection.newClientConnection("127.0.0.1", 999);
                allConnections.add(newConnection);
            }

            for(Connection connection : allConnections) {
                connection.sendData(new Payload("asdf", "/test/expensive"));
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for(Connection connection : allConnections) {
                connection.shutDownConnection();
            }

            primaryServer.shutDownServer();
            secondaryServer.shutDownServer();

        } catch (DuplicateTaskException e) {
            e.printStackTrace();
        } finally {
//            System.exit(0);
        }
    }

}
