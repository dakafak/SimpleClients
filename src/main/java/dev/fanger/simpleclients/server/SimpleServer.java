package dev.fanger.simpleclients.server;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.RemoveClientTask;
import dev.fanger.simpleclients.server.data.task.Task;
import dev.fanger.simpleclients.server.data.task.type.DefaultTaskTypes;
import dev.fanger.simpleclients.server.handlerthreads.ConnectionService;
import dev.fanger.simpleclients.server.handlerthreads.datahelper.ConnectionReceiveDataHelper;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleServer {

    private ConnectionService connectionService;

    private ConcurrentHashMap<UUID, Connection> clients;
    private ConcurrentHashMap<UUID, ConnectionReceiveDataHelper> connectionReceiveDataHelpers;
    private ConcurrentHashMap<Enum, Task> tasks;
    private int port;

    public SimpleServer(int port){
        this.port = port;

        clients = new ConcurrentHashMap<>();
        connectionReceiveDataHelpers = new ConcurrentHashMap<>();
        tasks = new ConcurrentHashMap<>();

        tasks.put(DefaultTaskTypes.DISCONNECT_CLIENT, new RemoveClientTask(clients, connectionReceiveDataHelpers));
    }

    public void startListeningForConnections(){
        connectionService = new ConnectionService(port, clients, connectionReceiveDataHelpers, tasks);
        connectionService.setContinueRunning(true);
        Thread connectionThread = new Thread(connectionService);
        connectionThread.start();
    }

    public void shutDownServer(){
        connectionService.setContinueRunning(false);
    }

    public boolean sendPayloadToClient(Connection connection, Payload payload){
        if(!connection.clientShouldBeDestroyed()){
            connection.sendData(payload);
            return true;
        }

        return false;
    }

    public Connection getClient(UUID id){
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

    public ConcurrentHashMap<UUID, Connection> getClients() {
        return clients;
    }

    public void setClients(ConcurrentHashMap<UUID, Connection> clients) {
        this.clients = clients;
    }

    public ConcurrentHashMap<UUID, ConnectionReceiveDataHelper> getConnectionReceiveDataHelpers() {
        return connectionReceiveDataHelpers;
    }

    public void setConnectionReceiveDataHelpers(ConcurrentHashMap<UUID, ConnectionReceiveDataHelper> connectionReceiveDataHelpers) {
        this.connectionReceiveDataHelpers = connectionReceiveDataHelpers;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
