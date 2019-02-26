package server.handlerthreads;

import connection.Connection;
import connection.Id;
import server.data.Payload;
import server.data.payloads.ConnectionPayload;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionService implements Runnable {

    private int numberOfThreads;
    private int port;
    private boolean continueRunning;

    private ServerSocket serverSocket;

    private ConcurrentLinkedQueue<Connection> clientsToValidate;

    public ConnectionService(int numberOfThreads,
                             int port,
                             ConcurrentLinkedQueue<Connection> clientsToValidate){
        this.numberOfThreads = numberOfThreads;
        this.port = port;
        this.clientsToValidate = clientsToValidate;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);

            while(continueRunning){
                Socket newClientSocket = serverSocket.accept();
                System.out.println("Accepted a new client");
                Connection newClientConnection = new Connection(newClientSocket);
                System.out.println("Created new connection object for new client");
                ConnectionPayload connectionPayload = (ConnectionPayload) newClientConnection.retrieveData();

                if(connectionPayload != null && connectionPayload.isValid()) {
                    clientsToValidate.add(newClientConnection);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setContinueRunning(boolean continueRunning) {
        this.continueRunning = continueRunning;
    }

}
