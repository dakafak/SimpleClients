package dev.fanger.simpleclients.server.handlerthreads;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.logging.Level;
import dev.fanger.simpleclients.logging.Logger;
import dev.fanger.simpleclients.server.data.task.Task;
import dev.fanger.simpleclients.server.handlerthreads.datahelper.ConnectionReceiveDataHelper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionService implements Runnable {

    private int port;
    private boolean continueRunning = true;

    private ServerSocket serverSocket;

    private ConcurrentHashMap<UUID, Connection> clients;
    private ConcurrentHashMap<UUID, ConnectionReceiveDataHelper> connectionReceiveDataHelpers;
    private ConcurrentHashMap<String, Task> tasks;

    public ConnectionService(int port,
                             ConcurrentHashMap<UUID, Connection> clients,
                             ConcurrentHashMap<UUID, ConnectionReceiveDataHelper> connectionReceiveDataHelpers,
                             ConcurrentHashMap<String, Task> tasks){
        this.port = port;
        this.clients = clients;
        this.connectionReceiveDataHelpers = connectionReceiveDataHelpers;
        this.tasks = tasks;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);

            while(continueRunning){
                Socket newClientSocket = serverSocket.accept();

                // Setup new connection with newly accepted client
                Connection newClientConnection = new Connection(newClientSocket);
                Logger.log(Level.INFO, "Accepted a new client: " + newClientConnection.getId());
                clients.put(newClientConnection.getId(), newClientConnection);

                // Setup data helper for new connection
                ConnectionReceiveDataHelper connectionReceiveDataHelper = new ConnectionReceiveDataHelper(newClientConnection, tasks);
                Thread connectionReceiveDataHelperThread = new Thread(connectionReceiveDataHelper);
                connectionReceiveDataHelperThread.start();
                connectionReceiveDataHelpers.put(newClientConnection.getId(), connectionReceiveDataHelper);

                // Add watcher to shut down and remove clients after they've completed running
                Thread connectionWatcherThread = new Thread(new ConnectionWatcher(newClientConnection.getId(), connectionReceiveDataHelperThread));
                connectionWatcherThread.start();

                // Log status of total clients so far
                Logger.log(Level.DEBUG, "Current client list size: " + clients.keySet().size());
            }
        } catch (IOException e) {
            Logger.log(Level.ERROR, e);
        }

        Logger.log(Level.INFO, "shutdown");
    }

    public void shutdown() {
        this.continueRunning = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            Logger.log(Level.ERROR, e);
        }
    }

    /**
     * Used to guarantee the shutdown of connections and data helper threads
     * This is used in an additional thread that waits until the ConnectionReceiveDataHelperThread completes
     *  but using Thread.join()
     */
    private class ConnectionWatcher implements Runnable {

        private UUID connectionId;
        private Thread connectionReceiveDataHelperThread;

        public ConnectionWatcher(UUID connectionId,
                                 Thread connectionReceiveDataHelperThread) {
            this.connectionId = connectionId;
            this.connectionReceiveDataHelperThread = connectionReceiveDataHelperThread;
        }

        @Override
        public void run() {
            try {
                // Wait for the connection to disconnect
                connectionReceiveDataHelperThread.join();
            } catch (InterruptedException e) {
                Logger.log(Level.DEBUG, e);
            } finally {
                if(connectionId != null) {
                    Logger.log(Level.INFO, "Disconnected client: " + connectionId);

                    if (clients != null && clients.containsKey(connectionId)) {
                        if(clients.get(connectionId) != null) {
                            clients.get(connectionId).shutDownClient();
                        }
                        clients.remove(connectionId);
                    }

                    if(connectionReceiveDataHelpers != null && connectionReceiveDataHelpers.containsKey(connectionId)) {
                        connectionReceiveDataHelpers.remove(connectionId);
                    }
                }
            }
        }

    }

}
