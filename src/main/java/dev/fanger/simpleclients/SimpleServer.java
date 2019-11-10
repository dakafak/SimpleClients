package dev.fanger.simpleclients;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.logging.Logger;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;
import dev.fanger.simpleclients.server.handlerthreads.ConnectionService;
import dev.fanger.simpleclients.server.handlerthreads.datahelper.ConnectionReceiveDataHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleServer extends SimpleClientManager {

    private ConnectionService connectionService;

    private ConcurrentHashMap<UUID, Connection> clients;
    private ConcurrentHashMap<UUID, ConnectionReceiveDataHelper> connectionReceiveDataHelpers;
    private int port;

    private SimpleServer(Builder builder) {
        clients = new ConcurrentHashMap<>();
        connectionReceiveDataHelpers = new ConcurrentHashMap<>();

        this.port = builder.getPort();
        overrideLoggerType(builder.getLoggerClassType());

        for(Task task : builder.getTasks()) {
            addTask(task);
        }
    }

    public void startListeningForConnections(){
        connectionService = new ConnectionService(port, clients, connectionReceiveDataHelpers, getTasks());
        Thread connectionThread = new Thread(connectionService);
        connectionThread.start();
    }

    public void shutDownServer(){
        connectionService.shutdown();
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

    private void overrideLoggerType(Class<? extends Logger> loggerClassType) {
        Logger.overrideLoggerType(loggerClassType);
    }

    public ConcurrentHashMap<UUID, Connection> getClients() {
        return clients;
    }

    public int getPort() {
        return port;
    }

    public static class Builder {

        private int port;
        private Class<? extends Logger> loggerClassType;
        private List<Task> tasks;

        public Builder(int port) {
            this.port = port;
            tasks = new LinkedList<>();
        }

        public Builder withLoggingType(Class<? extends Logger> loggerClassType) {
            this.loggerClassType = loggerClassType;
            return this;
        }

        public Builder withTask(Task task) {
            tasks.add(task);
            return this;
        }

        public SimpleServer build() {
            return new SimpleServer(this);
        }

        public int getPort() {
            return port;
        }

        public Class<? extends Logger> getLoggerClassType() {
            return loggerClassType;
        }

        public List<Task> getTasks() {
            return tasks;
        }

    }

}
