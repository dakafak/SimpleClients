package server;

import connection.Connection;
import connection.Id;
import server.data.Payload;
import server.handlerthreads.ConnectionService;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionHandler {

    private ConnectionService connectionService;

    private ConcurrentHashMap<Id, Connection> clients;
    private int numberOfThreads;
    private int port;

    public ConnectionHandler(int numberOfThreads, int port){
        this.numberOfThreads = numberOfThreads;
        this.port = port;

        clients = new ConcurrentHashMap<>();
    }

    public void startListeningForConnections(Payload connectionSuccessPayload){
        connectionService = new ConnectionService(numberOfThreads, port, clients, connectionSuccessPayload);
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
