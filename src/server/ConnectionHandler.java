package server;

import connection.Connection;
import connection.Id;
import server.data.payload.Payload;
import server.data.task.Task;
import server.handlerthreads.ConnectionService;
import server.handlerthreads.datahelper.ConnectionReceiveDataHelper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionHandler {

    private ConnectionService connectionService;

    private ConcurrentHashMap<Id, Connection> clients;
    private ConcurrentHashMap<Id, ConcurrentLinkedQueue<Payload>> outputPayloadQueuePerConnectionId;
    private ConcurrentHashMap<Id, ConnectionReceiveDataHelper> connectionReceiveDataHelpers;
    private ConcurrentHashMap<Enum, Task> tasks;
    private int port;

    public ConnectionHandler(int port){
        this.port = port;

        clients = new ConcurrentHashMap<>();
        outputPayloadQueuePerConnectionId = new ConcurrentHashMap<>();
        connectionReceiveDataHelpers = new ConcurrentHashMap<>();
        tasks = new ConcurrentHashMap<>();
    }

    public void startListeningForConnections(){
        connectionService = new ConnectionService(port, clients, connectionReceiveDataHelpers, tasks);
        connectionService.setContinueRunning(true);
        Thread connectionThread = new Thread(connectionService);
        connectionThread.start();
    }

    public void stopListeningForConnections(){
        connectionService.setContinueRunning(false);
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
        tasks.put(taskType, task);
    }

    public ConnectionService getConnectionService() {
        return connectionService;
    }

    public void setConnectionService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    public ConcurrentHashMap<Id, Connection> getClients() {
        return clients;
    }

    public void setClients(ConcurrentHashMap<Id, Connection> clients) {
        this.clients = clients;
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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
