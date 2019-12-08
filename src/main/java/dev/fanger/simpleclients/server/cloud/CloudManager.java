package dev.fanger.simpleclients.server.cloud;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.logging.Level;
import dev.fanger.simpleclients.logging.Logger;
import dev.fanger.simpleclients.server.ServerConnectionInfo;
import dev.fanger.simpleclients.server.data.payload.Payload;

import java.util.HashMap;
import java.util.List;

/**
 * The purpose of internal cloud managing in SimpleClients is to minimize CPU time for certain tasks. SimpleClients can
 * send data to process to other servers in the cluster to be executed by their tasks. A certain
 * {@link #THRESHOLD_TO_REQUEST_CLOUD_HELP} will need to be broken before trying to send data to another server. It will
 * then determine the best server to send data to based on the {@link #currentServerLoad} for each server in the
 * cluster.
 */
public class CloudManager {

    //TODO leader may not be needed, could have any server try to reach out to any server if current load passes a certain threshold
    /**
     * Whether or not this server is the leader of the connected servers
     */
//    private boolean leader;

    //TODO create task to return current server load
    private static int THRESHOLD_TO_REQUEST_CLOUD_HELP = 50;

    /**
     * TODO
     * Incremented when a task starts execution and decremented when a task finishes execution
     */
    private int currentServerLoad;

    /**
     * All other connections to other servers in the cloud
     */
    private HashMap<ServerConnectionInfo, Connection> cloudConnections;

    /**
     * TODO add this for determining random task execution division
     * TODO change this to orderable hashmap and sort based on load to pick the lowest to send executions to
     * Used to determine which server in the cloud to send the next task to
     */
    private HashMap<ServerConnectionInfo, Integer> cloudConnectionLoad;

    public CloudManager(List<ServerConnectionInfo> cloudConnectionInfo) {
        prepareCloudConnections(cloudConnectionInfo);
    }

    private void prepareCloudConnections(List<ServerConnectionInfo> cloudConnectionInfo) {
        cloudConnections = new HashMap<>();

        for(ServerConnectionInfo serverConnectionInfo : cloudConnectionInfo) {
            cloudConnections.put(serverConnectionInfo, null);
        }

        checkAllCloudConnections();

//        checkForLeader();
    }

    public void checkAllCloudConnections() {
        for(ServerConnectionInfo serverConnectionInfo : cloudConnections.keySet()) {
            checkAndTryToReconnectCloudConnection(serverConnectionInfo);
        }
    }

    /**
     * Checks cloud connections for disconnected cloud connection. Reconnects if the {@link Connection} is null.
     */
    private void checkAndTryToReconnectCloudConnection(ServerConnectionInfo serverConnectionInfo) {
        Connection connection = cloudConnections.get(serverConnectionInfo);
        //TODO probably need to do another check besides null to determine if connection is open
        if(connection == null || connection.connectionShouldBeDestroyed()) {
            if(connection != null) {
                connection.shutDownConnection();
            }

            Connection newCloudServerConnection = Connection.newClientConnection(serverConnectionInfo.getIp(), serverConnectionInfo.getPort());
            cloudConnections.replace(serverConnectionInfo, newCloudServerConnection);
        }
    }

    /**
     * Looks through all cloud connections for an existing leader, if no leader exists, sets self as leader
     */
//    private void checkForLeader() {
//
//    }

    //TODO these probably don't have to be synchronized??
    public synchronized void incrementServerLoad() {
        currentServerLoad++;
    }

    public synchronized void decrementServerLoad() {
        currentServerLoad--;
    }

    public int getCurrentServerLoad() {
        return currentServerLoad;
    }

    private boolean hasServerToSendDataTo() {
        for(Connection connection : cloudConnections.values()) {
            if(connection != null) {
                return true;
            }
        }

        return false;
    }

    public boolean shouldSendDataToAnotherServer() {
        checkAllCloudConnections();//TODO reconsider checking this every time it tries to send a message and use a periodically checked method like every 10 seconds

        if(!hasServerToSendDataTo()) {
            return false;
        }

        //TODO change this to check load values
        return Math.random() < .5;
    }

    public Connection getServerToExecuteData() {
        //TODO have this decide based on load of other servers instead of grabbing the first one
        for(Connection connection : cloudConnections.values()) {
            if(connection != null) {
                return connection;
            }
        }

        Logger.log(Level.WARN, "Could not find another server to send execution to");
        return null;
    }

    public void executePayloadOnAnotherServer(Payload payload) {
        Connection serverToSendDataTo = getServerToExecuteData();

        if(serverToSendDataTo != null) {
            getServerToExecuteData().sendData(payload);
        }
    }

    public Payload getExecutedDataFromAnotherServer(Payload payload) {
        Connection serverToSendDataTo = getServerToExecuteData();

        if(serverToSendDataTo != null) {
            serverToSendDataTo.sendData(payload);
            return serverToSendDataTo.retrieveData();
        }

        return null;
    }

}
