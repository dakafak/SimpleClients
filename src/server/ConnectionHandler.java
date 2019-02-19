package server;

import client.Client;
import client.ClientId;
import client.ClientValidator;
import server.data.Payload;
import server.handlerthreads.ConnectionService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

public class ConnectionHandler {

    private ConnectionService connectionService;

    //TODO change this to a synchronized linked list and pass as parameter into connection service
    //      ConnectionHandler -> ConnectionService
    //      ConnectionHandler -> DataTransferService
//    private SynchronousQueue<Client> allClients;//TODO this should be changed to a synchronous list or hashmap
    private ConcurrentHashMap<ClientId, Client> clients;
    private ClientValidator clientValidator;
    private int numberOfThreads;
    private int port;

    public ConnectionHandler(int numberOfThreads, int port, ClientValidator clientValidator){
        this.numberOfThreads = numberOfThreads;
        this.port = port;
        this.clientValidator = clientValidator;

        clients = new ConcurrentHashMap<>();
    }

    public void startListeningForConnections(){
        connectionService = new ConnectionService(numberOfThreads, port, clients, clientValidator);
        connectionService.setContinueRunning(true);
        Thread connectionThread = new Thread(connectionService);
        connectionThread.start();
    }

    public void stopListeningForConnections(){
        connectionService.setContinueRunning(false);
    }

    public void updateAllClientsWithPayload(Payload payload){
        if(payload.isValid()) {
            for (Client client : clients.values()) {//TODO this won't be thread safe -- update this
                if(!client.clientShouldBeDestroyed()) {
                    client.sendData(payload);
                }
            }
        }
    }

    public void updateClientWithPayload(Client client, Payload payload){
        if(payload.isValid() && !client.clientShouldBeDestroyed()){
            client.sendData(payload);
        }
    }

}
