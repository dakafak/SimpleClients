package server;

import client.ClientConnection;
import server.handlerthreads.ConnectionThread;

import java.util.LinkedList;

public class ConnectionHandler {

    private LinkedList<ClientConnection> activeConnections;

    public ConnectionHandler(int numberOfThreads, int port){
        ConnectionThread connectionThread = new ConnectionThread(numberOfThreads, port);
    }

}
