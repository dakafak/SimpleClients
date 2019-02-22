package server.handlerthreads;

import connection.Connection;
import connection.Id;
import connection.ClientValidator;
import server.data.Payload;
import server.data.payloads.ConnectionPayload;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionService implements Runnable {

    private int numberOfThreads;
    private int port;
    private boolean continueRunning;

    private ServerSocket serverSocket;

//    private LinkedList<Connection> clientsToValidate;
    private ConcurrentHashMap<Id, Connection> allClientsReference;
//    private ClientValidator clientValidator;
    private Payload connectionSuccessPayload;

    public ConnectionService(int numberOfThreads,
                             int port,
                             ConcurrentHashMap<Id, Connection> allClientsReference,
                             ClientValidator clientValidator,
                             Payload connectionSuccessPayload){
        this.numberOfThreads = numberOfThreads;
        this.port = port;
        this.allClientsReference = allClientsReference;
//        this.clientValidator = clientValidator;
        this.connectionSuccessPayload = connectionSuccessPayload;

//        clientsToValidate = new LinkedList<>();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);

            while(continueRunning){
                Socket newClientSocket = serverSocket.accept();
                System.out.println("Accepted a new client");
                Connection newClientConnection = new Connection(newClientSocket);
                //TODO client validation shouldn't happen here, it should be done in the queue below
                //      in the connection payload test -- remove client validator
                System.out.println("Created new connection object for new client");
//                clientsToValidate.add(newClientConnection);
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

//    public void validateNewClientsAndAddToAllClients(){
//        for(Connection connection : clientsToValidate){
//            connection.sendData(connectionSuccessPayload);
//            ConnectionPayload connectionPayload = (ConnectionPayload) connection.retrieveData();
//
//            if(connectionPayload != null && connectionPayload.isValid()) {
//                connection.setId(connectionPayload.getId());
//                allClientsReference.put(connection.getId(), connection);
//            }
//
//            clientsToValidate.remove(connection);
//        }
//    }

    public boolean isContinueRunning() {
        return continueRunning;
    }

    public void setContinueRunning(boolean continueRunning) {
        this.continueRunning = continueRunning;
    }

}
