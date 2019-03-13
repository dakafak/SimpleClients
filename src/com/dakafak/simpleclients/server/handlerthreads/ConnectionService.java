package com.dakafak.simpleclients.server.handlerthreads;

import com.dakafak.simpleclients.connection.Connection;
import com.dakafak.simpleclients.connection.Id;
import com.dakafak.simpleclients.server.data.task.Task;
import com.dakafak.simpleclients.server.handlerthreads.datahelper.ConnectionReceiveDataHelper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionService implements Runnable {

    private int port;
    private boolean continueRunning;

    private ServerSocket serverSocket;

    private ConcurrentHashMap<Id, Connection> clients;
    private ConcurrentHashMap<Id, ConnectionReceiveDataHelper> connectionReceiveDataHelpers;
    private ConcurrentHashMap<Enum, Task> tasks;

    public ConnectionService(int port,
                             ConcurrentHashMap<Id, Connection> clients,
                             ConcurrentHashMap<Id, ConnectionReceiveDataHelper> connectionReceiveDataHelpers,
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
                System.out.println("Accepted a new client");

                Connection newClientConnection = new Connection(newClientSocket);
                newClientConnection.setId(new Id((long)Math.random()*100));
                clients.put(newClientConnection.getId(), newClientConnection);

                ConnectionReceiveDataHelper connectionReceiveDataHelper = new ConnectionReceiveDataHelper(newClientConnection, tasks);
                connectionReceiveDataHelper.setContinueRunning(true);
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
