package server;

import connection.Connection;
import connection.Id;
import connection.ClientValidator;
import server.data.Payload;
import server.data.PayloadValidator;
import server.data.payloads.ConnectionPayload;
import server.handlerthreads.ConnectionService;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionHandler {

    private ConnectionService connectionService;

    //TODO change this to a synchronized linked list and pass as parameter into connection service
    //      ConnectionHandler -> ConnectionService
    //      ConnectionHandler -> DataTransferService
//    private SynchronousQueue<Connection> allClients;//TODO this should be changed to a synchronous list or hashmap
    private ConcurrentHashMap<Id, Connection> clients;
    private ClientValidator clientValidator;
    private int numberOfThreads;
    private int port;

    public ConnectionHandler(int numberOfThreads, int port, ClientValidator clientValidator){
        this.numberOfThreads = numberOfThreads;
        this.port = port;
        this.clientValidator = clientValidator;

        clients = new ConcurrentHashMap<>();
    }

    public void startListeningForConnections(Payload connectionSuccessPayload){
        connectionService = new ConnectionService(numberOfThreads, port, clients, clientValidator, connectionSuccessPayload);
        connectionService.setContinueRunning(true);
//        Thread connectionThread = new Thread(connectionService);
//        connectionThread.start();
        connectionService.run();
    }

    public void stopListeningForConnections(){
        connectionService.setContinueRunning(false);
    }

    public void updateAllClientsWithPayload(Payload payload){
        if(payload.isValid()) {
            for (Connection connection : clients.values()) {//TODO this won't be thread safe -- update this
                if(!connection.clientShouldBeDestroyed()) {

                    connection.sendData(payload);
                }
            }
        }
    }

    public void updateClientWithPayload(Connection connection, Payload payload){
        if(payload.isValid() && !connection.clientShouldBeDestroyed()){
            connection.sendData(payload);
        }
    }

}
