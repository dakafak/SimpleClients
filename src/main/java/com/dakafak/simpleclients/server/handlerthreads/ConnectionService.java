package com.dakafak.simpleclients.server.handlerthreads;

import com.dakafak.simpleclients.connection.Connection;
import com.dakafak.simpleclients.server.data.task.Task;
import com.dakafak.simpleclients.server.handlerthreads.datahelper.ConnectionReceiveDataHelper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionService implements Runnable {

    private int port;
    private boolean continueRunning;

    private ServerSocket serverSocket;

    private ConcurrentHashMap<UUID, Connection> clients;
    private ConcurrentHashMap<UUID, ConnectionReceiveDataHelper> connectionReceiveDataHelpers;
    private ConcurrentHashMap<Enum, Task> tasks;

    public ConnectionService(int port,
                             ConcurrentHashMap<UUID, Connection> clients,
                             ConcurrentHashMap<UUID, ConnectionReceiveDataHelper> connectionReceiveDataHelpers,
                             ConcurrentHashMap<Enum, Task> tasks){
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

                Connection newClientConnection = new Connection(newClientSocket);
                System.out.println("Accepted a new client: " + newClientConnection.getId());
                clients.put(newClientConnection.getId(), newClientConnection);

                ConnectionReceiveDataHelper connectionReceiveDataHelper = new ConnectionReceiveDataHelper(newClientConnection, tasks);
                Thread connectionReceiveDataHelperThread = new Thread(connectionReceiveDataHelper);
                connectionReceiveDataHelperThread.start();
                connectionReceiveDataHelpers.put(newClientConnection.getId(), connectionReceiveDataHelper);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setContinueRunning(boolean continueRunning) {
        this.continueRunning = continueRunning;
    }

}
