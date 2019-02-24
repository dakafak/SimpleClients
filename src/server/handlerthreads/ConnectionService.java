package server.handlerthreads;

import connection.Connection;
import connection.Id;
import server.data.Payload;
import server.data.payloads.ConnectionPayload;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionService implements Runnable {

    private int numberOfThreads;
    private int port;
    private boolean continueRunning;

    private ServerSocket serverSocket;

    private ConcurrentHashMap<Id, Connection> allClientsReference;
    private Payload connectionSuccessPayload;

    public ConnectionService(int numberOfThreads,
                             int port,
                             ConcurrentHashMap<Id, Connection> allClientsReference,
                             Payload connectionSuccessPayload){
        this.numberOfThreads = numberOfThreads;
        this.port = port;
        this.allClientsReference = allClientsReference;
        this.connectionSuccessPayload = connectionSuccessPayload;
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
                newClientConnection.sendData(connectionSuccessPayload);
                ConnectionPayload connectionPayload = (ConnectionPayload) newClientConnection.retrieveData();

                if(connectionPayload != null && connectionPayload.isValid()) {
                    newClientConnection.setId(connectionPayload.getId());
                    allClientsReference.put(newClientConnection.getId(), newClientConnection);
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
