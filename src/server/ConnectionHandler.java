package server;

import connection.Connection;
import connection.Id;
import server.data.datatypes.ConnectionPayloadPair;
import server.data.payload.Payload;
import server.data.task.Task;
import server.handlerthreads.ConnectionService;
import server.handlerthreads.ConnectionValidatorService;
import server.handlerthreads.DataTransferService;
import server.handlerthreads.TaskExecutorService;
import server.handlerthreads.datahelper.ConnectionReceiveDataHelper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionHandler {

    private ConnectionService connectionService;
    private ConnectionValidatorService connectionValidatorService;
    private DataTransferService dataTransferService;
    private TaskExecutorService taskExecutorService;

    private ConcurrentHashMap<Id, Connection> clients;
    private ConcurrentLinkedQueue<Connection> clientsToValidate;
    private ConcurrentHashMap<Id, ConcurrentLinkedQueue<Payload>> outputPayloadQueuePerConnectionId;
    private ConcurrentHashMap<Id, ConnectionReceiveDataHelper> connectionReceiveDataHelpers;
    private ConcurrentLinkedQueue<ConnectionPayloadPair> inputQueue;
    private int port;

    public ConnectionHandler(int port){
        this.port = port;

        clients = new ConcurrentHashMap<>();
        clientsToValidate = new ConcurrentLinkedQueue<>();
        outputPayloadQueuePerConnectionId = new ConcurrentHashMap<>();
        connectionReceiveDataHelpers = new ConcurrentHashMap<>();
        inputQueue = new ConcurrentLinkedQueue<>();
    }

    public void startListeningForConnections(){
        connectionService = new ConnectionService(port, clientsToValidate);
        connectionService.setContinueRunning(true);
        Thread connectionThread = new Thread(connectionService);
        connectionThread.start();
    }

    public void startValidatingClients(){
        connectionValidatorService = new ConnectionValidatorService(clients, clientsToValidate, connectionReceiveDataHelpers, inputQueue, taskExecutorService);
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

    public void startTaskExecutorService(int numberOfThreads, long timeoutInMilliseconds){
        taskExecutorService = new TaskExecutorService(numberOfThreads, timeoutInMilliseconds);
        taskExecutorService.setContinueRunning(true);
        Thread taskExecutorServiceThread = new Thread(taskExecutorService);
        taskExecutorServiceThread.start();
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
                    continue;
                }

                if(!connection.clientShouldBeDestroyed()) {
                    if(!outputPayloadQueuePerConnectionId.containsKey(connection.getId())) {
                        ConcurrentLinkedQueue<Payload> payloadConcurrentLinkedQueue = new ConcurrentLinkedQueue<>();
                        outputPayloadQueuePerConnectionId.put(connection.getId(), payloadConcurrentLinkedQueue);
                    }

                    outputPayloadQueuePerConnectionId.get(connection.getId()).add(payload);
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

    public Connection getClient(Id id){
        if(id != null && clients.containsKey(id)) {
            return clients.get(id);
        }

        return null;
    }

    public void addTask(Enum taskType, Task task){
        if(taskExecutorService != null) {
            taskExecutorService.addTask(taskType, task);
        } else {
            System.out.println("-- ERROR Tried adding a task before task executor service was setup");
        }
    }

    public ConnectionService getConnectionService() {
        return connectionService;
    }

    public void setConnectionService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    public ConnectionValidatorService getConnectionValidatorService() {
        return connectionValidatorService;
    }

    public void setConnectionValidatorService(ConnectionValidatorService connectionValidatorService) {
        this.connectionValidatorService = connectionValidatorService;
    }

    public DataTransferService getDataTransferService() {
        return dataTransferService;
    }

    public void setDataTransferService(DataTransferService dataTransferService) {
        this.dataTransferService = dataTransferService;
    }

    public ConcurrentHashMap<Id, Connection> getClients() {
        return clients;
    }

    public void setClients(ConcurrentHashMap<Id, Connection> clients) {
        this.clients = clients;
    }

    public ConcurrentLinkedQueue<Connection> getClientsToValidate() {
        return clientsToValidate;
    }

    public void setClientsToValidate(ConcurrentLinkedQueue<Connection> clientsToValidate) {
        this.clientsToValidate = clientsToValidate;
    }

    public ConcurrentHashMap<Id, ConcurrentLinkedQueue<Payload>> getOutputPayloadQueuePerConnectionId() {
        return outputPayloadQueuePerConnectionId;
    }

    public void setOutputPayloadQueuePerConnectionId(ConcurrentHashMap<Id, ConcurrentLinkedQueue<Payload>> outputPayloadQueuePerConnectionId) {
        this.outputPayloadQueuePerConnectionId = outputPayloadQueuePerConnectionId;
    }

    public ConcurrentHashMap<Id, ConnectionReceiveDataHelper> getConnectionReceiveDataHelpers() {
        return connectionReceiveDataHelpers;
    }

    public void setConnectionReceiveDataHelpers(ConcurrentHashMap<Id, ConnectionReceiveDataHelper> connectionReceiveDataHelpers) {
        this.connectionReceiveDataHelpers = connectionReceiveDataHelpers;
    }

    public ConcurrentLinkedQueue<ConnectionPayloadPair> getInputQueue() {
        return inputQueue;
    }

    public void setInputQueue(ConcurrentLinkedQueue<ConnectionPayloadPair> inputQueue) {
        this.inputQueue = inputQueue;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
