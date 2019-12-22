package dev.fanger.simpleclients.cloud;

import dev.fanger.simpleclients.SimpleServer;
import dev.fanger.simpleclients.TraditionalClient;
import dev.fanger.simpleclients.examples.server.tasks.ExpensiveTask;
import dev.fanger.simpleclients.examples.server.tasks.PingTask;
import dev.fanger.simpleclients.exceptions.DuplicateTaskException;
import dev.fanger.simpleclients.logging.loggers.SystemPrintTimeLogger;
import dev.fanger.simpleclients.server.ServerConnectionInfo;
import dev.fanger.simpleclients.server.data.payload.Payload;

import java.util.ArrayList;
import java.util.List;

public class CloudTest {

    public static void main(String[] args) {
        new CloudTest();
    }

    private SimpleServer primaryServer;
    private SimpleServer[] additionalServers;
    private int numberOfAdditionalServers = 3;
    private int numberOfClientsToPingServers = 50;
    private int numberOfPayloadsToSend = 1;
    private int[] allServerPorts;

    public CloudTest() {
        //TODO also add test for return data from expensive tasks
        //TODO probably want to consider adding unit tests to cloud manager and task load manager
        // might be easier to solve some problems

        // Setup servers
        allServerPorts = new int[numberOfAdditionalServers + 1];
        allServerPorts[0] = 999;
        for(int i = 1; i < allServerPorts.length; i++) {
            allServerPorts[i] = 1000 + i;
        }
        primaryServer = getNewSimpleServer(allServerPorts[0]);

        additionalServers = new SimpleServer[numberOfAdditionalServers];
        for(int i = 0; i < additionalServers.length; i++) {
            additionalServers[i] = getNewSimpleServer(allServerPorts[i + 1]);
        }

        // Start servers
        primaryServer.startListeningForConnections();
        for(SimpleServer simpleServer : additionalServers) {
            simpleServer.startListeningForConnections();
        }

        // Wait for servers to connect to each other
        while(!allServersAreConnected()) {
            System.out.println("Waiting to fully connect servers");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Run tests
        createClientsAndRunTests();

//        long startTime = System.currentTimeMillis();
//        while(System.currentTimeMillis() < startTime + 30_000) {
//            primaryServer.getCloudManager().printCloudLoadStatus(ExpensiveTask.class);
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

        while(!allServersHaveEmptyServerLoad()) {
            System.out.println("Waiting for tasks to finish");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Shut down
        System.out.println("--------------------------------------------------------------------------------" +
                "Shutting down servers--------------------------------------------------------");
        shutdownServers();
        System.exit(0);
    }

    private boolean allServersHaveEmptyServerLoad() {
        if(!primaryServer.getCloudManager().hasEmptyTaskQueues()) {
            return false;
        }

        for(SimpleServer simpleServer : additionalServers) {
            if(!simpleServer.getCloudManager().hasEmptyTaskQueues()) {
                return false;
            }
        }

        return true;
    }

    private boolean allServersAreConnected() {
        if(!primaryServer.getCloudManager().isFullyConnectedToCloudServers()) {
            return false;
        }

        for(SimpleServer simpleServer : additionalServers) {
            if(!simpleServer.getCloudManager().isFullyConnectedToCloudServers()) {
                return false;
            }
        }

        return true;
    }

    private SimpleServer getNewSimpleServer(int port) {
        try {
            SimpleServer.Builder simpleServerBuilder = new SimpleServer.Builder(port)
                    .withLoggingType(SystemPrintTimeLogger.class)
                    .withTask("/test/ping", new PingTask())
                    .withTask("/test/expensive", new ExpensiveTask(port));

            for(int otherPort : allServerPorts) {
                if(otherPort != port) {
                    simpleServerBuilder = simpleServerBuilder.withCloudConnectionInfo(
                            new ServerConnectionInfo("127.0.0.1", otherPort));
                }
            }

            SimpleServer simpleServer = simpleServerBuilder.build();
            System.out.println("Created new server: " + simpleServer);
            return simpleServer;
        } catch (DuplicateTaskException e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<ClientRunner> clientRunners;
    private List<Thread> clientRunnerThreads;
    private void createClientsAndRunTests() {
        clientRunners = new ArrayList<>();
        clientRunnerThreads = new ArrayList<>();

        // Create clients
        for(int i = 0; i < numberOfClientsToPingServers; i++) {
            ClientRunner clientRunner = new ClientRunner();
            clientRunners.add(clientRunner);
            Thread newClientRunnerThread = new Thread(clientRunner);
            clientRunnerThreads.add(newClientRunnerThread);
        }

        for(Thread clientRunnerThread : clientRunnerThreads) {
            clientRunnerThread.start();
        }

        for(Thread clientRunnerThread : clientRunnerThreads) {
            try {
                clientRunnerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(ClientRunner clientRunner : clientRunners) {
            clientRunner.shutDownClient();
        }
    }

    private void shutdownServers() {
        primaryServer.shutDownServer();
        for(SimpleServer simpleServer : additionalServers) {
            simpleServer.shutDownServer();
        }
    }

    class ClientRunner implements Runnable {

        private TraditionalClient traditionalClient;

        public ClientRunner() {
            traditionalClient = new TraditionalClient("127.0.0.1", 999);
        }

        @Override
        public void run() {
            for(int i = 0; i < numberOfPayloadsToSend; i++) {
                Payload expensiveTaskPayload = new Payload("Expensive", "/test/expensive");
                traditionalClient.sendData(expensiveTaskPayload);
            }
        }

        private void shutDownClient() {
            traditionalClient.shutDownClient();
        }

    }

}
