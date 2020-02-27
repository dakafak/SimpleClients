package dev.fanger.simpleclients.cloud;

import dev.fanger.simpleclients.SimpleServer;
import dev.fanger.simpleclients.TraditionalClient;
import dev.fanger.simpleclients.examples.server.tasks.ExpensiveTask;
import dev.fanger.simpleclients.examples.server.tasks.PingTask;
import dev.fanger.simpleclients.exceptions.DuplicateTaskException;
import dev.fanger.simpleclients.logging.loggers.SystemPrintTimeLogger;
import dev.fanger.simpleclients.server.ServerConnectionInfo;
import dev.fanger.simpleclients.server.data.payload.Payload;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CloudLoadTest {

    public static void main(String[] args) {
        try {
            (new CloudLoadTest()).testLoadTaskProcessing();
        } catch (DuplicateTaskException e) {
            e.printStackTrace();
        }
    }

    public void testLoadTaskProcessing() throws DuplicateTaskException {
        SimpleServer primaryServer = new SimpleServer.Builder(10000)
                .withLoggingType(SystemPrintTimeLogger.class)
                .withTask("/test/ping", new PingTask())
                .withTask("/test/expensive", new ExpensiveTask(10000))
                .withCloudConnectionInfo(new ServerConnectionInfo("127.0.0.1", 10001))
                .build();
        primaryServer.startListeningForConnections();

        SimpleServer secondaryServer = new SimpleServer.Builder(10001)
                .withLoggingType(SystemPrintTimeLogger.class)
                .withTask("/test/ping", new PingTask())
                .withTask("/test/expensive", new ExpensiveTask(10001))
                .withCloudConnectionInfo(new ServerConnectionInfo("127.0.0.1", 10000))
                .build();
        secondaryServer.startListeningForConnections();

        // Wait for servers to connect to each other
        while(!primaryServer.getCloudManager().isFullyConnectedToCloudServers() &&
                !secondaryServer.getCloudManager().isFullyConnectedToCloudServers()) {
            System.out.println("Waiting to fully connect servers");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        List<TraditionalClient> allClients = new ArrayList<>();

        // Create clients
        for(int i = 0; i < 50; i++) {
            allClients.add(new TraditionalClient("127.0.0.1", 10000));
        }

        // Send data to servers
        for(TraditionalClient traditionalClient : allClients) {
            for(int i = 0; i < 1; i++) {
                Payload expensiveTaskPayload = new Payload("Expensive", "/test/expensive");
                traditionalClient.sendData(expensiveTaskPayload);
            }
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Shut down clients
        for(TraditionalClient traditionalClient : allClients) {
            traditionalClient.shutDownClient();
        }

        System.out.println("Done");
    }

}
