package server;

import connection.Connection;
import connection.Id;
import server.data.payload.Payload;
import server.data.task.Task;
import server.handlerthreads.ConnectionService;
import server.handlerthreads.ConnectionValidatorService;
import server.handlerthreads.DataTransferService;
import server.handlerthreads.datahelper.ConnectionReceiveDataHelper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionHandler {

    private ConnectionService connectionService;
    private ConnectionValidatorService connectionValidatorService;
    private DataTransferService dataTransferService;

    private ConcurrentHashMap<Id, Connection> clients;
    private ConcurrentLinkedQueue<Connection> clientsToValidate;
    private ConcurrentHashMap<Id, ConcurrentLinkedQueue<Payload>> inputPayloadQueuePerConnectionId;
    private ConcurrentHashMap<Id, ConcurrentLinkedQueue<Payload>> outputPayloadQueuePerConnectionId;
    private ConcurrentHashMap<Id, ConnectionReceiveDataHelper> connectionReceiveDataHelpers;
    private ConcurrentHashMap<Enum, Task> tasks;
    private int numberOfThreads;
    private int port;

    public ConnectionHandler(int numberOfThreads, int port){
        this.numberOfThreads = numberOfThreads;
        this.port = port;

        clients = new ConcurrentHashMap<>();
        clientsToValidate = new ConcurrentLinkedQueue<>();
        inputPayloadQueuePerConnectionId = new ConcurrentHashMap<>();
        outputPayloadQueuePerConnectionId = new ConcurrentHashMap<>();
        connectionReceiveDataHelpers = new ConcurrentHashMap<>();
        tasks = new ConcurrentHashMap<>();
    }

    //TODO consider updating all start methods with threads to use a ExecutorService and the numberOfThreads value above
    public void startListeningForConnections(){
        connectionService = new ConnectionService(numberOfThreads, port, clientsToValidate);
        connectionService.setContinueRunning(true);
        Thread connectionThread = new Thread(connectionService);
        connectionThread.start();
    }

    public void startValidatingClients(){
        connectionValidatorService = new ConnectionValidatorService(clients, clientsToValidate, connectionReceiveDataHelpers, inputPayloadQueuePerConnectionId);
        connectionValidatorService.setContinueRunning(true);
        Thread connectionValidatorThread = new Thread(connectionValidatorService);
        connectionValidatorThread.start();
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

    public void sendPayloadListToClient(Connection connection, ConcurrentLinkedQueue<Payload> payloads){
        if(!outputPayloadQueuePerConnectionId.containsKey(connection.getId())){
            ConcurrentLinkedQueue<Payload> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
            outputPayloadQueuePerConnectionId.put(connection.getId(), concurrentLinkedQueue);
        }

        outputPayloadQueuePerConnectionId.get(connection.getId()).addAll(payloads);
    }

    public void sendPayloadToClient(Connection connection, Payload payload){
        if(payload.isValid() && !connection.clientShouldBeDestroyed()){
            connection.sendData(payload);
        }
    }

    public ConcurrentHashMap<Id, Connection> getClients() {
        return clients;
    }

    public Connection getClient(Id id){
        if(id != null && clients.containsKey(id)) {
            return clients.get(id);
        }

        return null;
    }

    public void addTask(Enum taskType, Task task){
        tasks.put(taskType, task);
    }

    public void setClients(ConcurrentHashMap<Id, Connection> clients) {
        this.clients = clients;
    }

    public ConcurrentHashMap<Id, ConcurrentLinkedQueue<Payload>> getInputPayloadQueuePerConnectionId() {
        return inputPayloadQueuePerConnectionId;
    }

    public ConcurrentHashMap<Id, ConcurrentLinkedQueue<Payload>> getOutputPayloadQueuePerConnectionId() {
        return outputPayloadQueuePerConnectionId;
    }
}
