package server;

import connection.Connection;
import connection.Id;
import server.data.Payload;
import server.handlerthreads.ConnectionService;
import server.handlerthreads.DataReceiveService;
import server.handlerthreads.DataTransferService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionHandler {

    private ConnectionService connectionService;
    private DataReceiveService dataReceiveService;
    private DataTransferService dataTransferService;

    private ConcurrentHashMap<Id, Connection> clients;
    private ConcurrentHashMap<Id, ConcurrentLinkedQueue<Payload>> inputPayloadQueuePerConnectionId;
    private ConcurrentHashMap<Id, ConcurrentLinkedQueue<Payload>> outputPayloadQueuePerConnectionId;
    private int numberOfThreads;
    private int port;

    public ConnectionHandler(int numberOfThreads, int port){
        this.numberOfThreads = numberOfThreads;
        this.port = port;

        clients = new ConcurrentHashMap<>();
        inputPayloadQueuePerConnectionId = new ConcurrentHashMap<>();
        outputPayloadQueuePerConnectionId = new ConcurrentHashMap<>();
    }

    //TODO consider updating all start methods with threads to use a ExecutorService and the numberOfThreads value above
    public void startListeningForConnections(Payload connectionSuccessPayload){
        connectionService = new ConnectionService(numberOfThreads, port, clients, connectionSuccessPayload);
        connectionService.setContinueRunning(true);
        Thread connectionThread = new Thread(connectionService);
        connectionThread.start();
//        connectionService.run();
    }

    //TODO consider updating all start methods with threads to use a ExecutorService and the numberOfThreads value above
    public void startRecievingDataFromClients(){
        dataReceiveService = new DataReceiveService(clients, inputPayloadQueuePerConnectionId);
        dataReceiveService.setContinueRunning(true);
        Thread dataReceiveServiceThread = new Thread(dataReceiveService);
        dataReceiveServiceThread.start();
    }

    //TODO consider updating all start methods with threads to use a ExecutorService and the numberOfThreads value above
    public void startSendingDataToClients(){
        dataTransferService = new DataTransferService(clients, outputPayloadQueuePerConnectionId);
        dataTransferService.setContinueRunning(true);
        Thread dataTransferServiceThread = new Thread(dataTransferService);
        dataTransferServiceThread.start();
    }

    public void stopListeningForConnections(){
        connectionService.setContinueRunning(false);
    }

    public void updateAllClientsWithPayload(Payload payload){
        if(payload.isValid()) {
            for (Connection connection : clients.values()) {//TODO this won't be thread safe -- update this
                if(connection.clientShouldBeDestroyed()) {
                    clients.remove(connection.getId());
                    outputPayloadQueuePerConnectionId.remove(connection.getId());
                    inputPayloadQueuePerConnectionId.remove(connection.getId());
                    continue;
                }

                if(!connection.clientShouldBeDestroyed()) {
                    if(!outputPayloadQueuePerConnectionId.containsKey(connection.getId())) {
                        ConcurrentLinkedQueue<Payload> payloadConcurrentLinkedQueue = new ConcurrentLinkedQueue<>();
                        outputPayloadQueuePerConnectionId.put(connection.getId(), payloadConcurrentLinkedQueue);
                    }

                    outputPayloadQueuePerConnectionId.get(connection.getId()).add(payload);
//                    connection.sendData(payload); TODO this will be done in the data transfer service
                }
            }
        }
    }

    public void updateClientWithPayload(Connection connection, Payload payload){
        if(payload.isValid() && !connection.clientShouldBeDestroyed()){
            connection.sendData(payload);
        }
    }

    public ConcurrentHashMap<Id, Connection> getClients() {
        return clients;
    }

    public void setClients(ConcurrentHashMap<Id, Connection> clients) {
        this.clients = clients;
    }

}
