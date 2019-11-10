package dev.fanger.simpleclients;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.logging.Logger;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.handlerthreads.ConnectionService;
import dev.fanger.simpleclients.server.handlerthreads.datahelper.ConnectionReceiveDataHelper;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleServer extends SimpleClientManager {

    private ConnectionService connectionService;

    private ConcurrentHashMap<UUID, Connection> clients;
    private ConcurrentHashMap<UUID, ConnectionReceiveDataHelper> connectionReceiveDataHelpers;
    private int port;

    public SimpleServer(int port){
        this.port = port;

        clients = new ConcurrentHashMap<>();
        connectionReceiveDataHelpers = new ConcurrentHashMap<>();
    }

    public void startListeningForConnections(){
        connectionService = new ConnectionService(port, clients, connectionReceiveDataHelpers, getTasks());
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

    public void overrideLoggerType(Class<? extends Logger> loggerClassType) {
        Logger.overrideLoggerType(loggerClassType);
    }

    public ConcurrentHashMap<UUID, Connection> getClients() {
        return clients;
    }

    public int getPort() {
        return port;
    }

    //TODO add a builder for this, rather than the current server building system

}
