package dev.fanger.simpleclients.server.cloud;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.logging.Level;
import dev.fanger.simpleclients.logging.Logger;
import dev.fanger.simpleclients.server.ServerConnectionInfo;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WARNING : Cloud functionality for SimpleClients is experimental
 *
 * The purpose of internal cloud managing in SimpleClients is to minimize CPU time for certain tasks. SimpleClients can
 * send data to process to other servers in the cluster to be executed by their tasks. A threshold for task max server
 * load will need to be broken before trying to send data to another server. It will then determine the best server to
 * send data to based on the {@link #taskLoadManager} for each server in the cluster.
 */
public class CloudManager {

    private int id;

    /**
     * All other connections to other servers in the cloud
     */
    private List<ServerConnectionInfo> cloudConnectionsToMake;
    private ConcurrentHashMap<ServerConnectionInfo, Connection> cloudConnections;

    private TaskLoadManager taskLoadManager;
    private CloudConnectionManager cloudConnectionManager;
    private Thread cloudStatusThread;

    public CloudManager(int id, List<ServerConnectionInfo> cloudConnectionInfo) {
        this.id = id;

        //TODO try using a separate internal SimpleServer specifically for cloud functionality that sits on a different port and only has load update tasks
        if(cloudConnectionInfo == null || cloudConnectionInfo.isEmpty()) {
            return;
        }

        cloudConnectionsToMake = new ArrayList<>(cloudConnectionInfo);
        cloudConnections = new ConcurrentHashMap<>();
    }

    public void start(Collection<Task> allTasks) {
        taskLoadManager = new TaskLoadManager(allTasks);

        cloudConnectionManager = new CloudConnectionManager();
        cloudStatusThread = new Thread(cloudConnectionManager);

        cloudStatusThread.start();
    }

    public boolean hasEmptyTaskQueues() {
        for(Class taskLoadClass : taskLoadManager.getTaskClassList()) {
            if(taskLoadManager.getCurrentServerLoad(taskLoadClass) > 0) {
                return false;
            }
        }

        return true;
    }

    private boolean hasServerToSendDataTo() {
        for(ServerConnectionInfo serverConnectionInfo : cloudConnections.keySet()) {
            if(cloudConnections.get(serverConnectionInfo) != null && !cloudConnections.get(serverConnectionInfo).connectionShouldBeDestroyed()) {
                return true;
            }
        }

        return false;
    }

    public void executePayloadOnAnotherServer(Payload payload) {
        Connection serverToSendDataTo = getServerToExecuteData();

        if(serverToSendDataTo != null) {
            serverToSendDataTo.sendData(payload);
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

    public boolean shouldSendDataToAnotherServer(Class taskClass, int maxLoadForTask) {
        cloudConnectionManager.checkAllCloudConnections();

        if(!hasServerToSendDataTo()) {
            return false;
        }

        return taskLoadManager.getCurrentServerLoad(taskClass) >= maxLoadForTask;
    }

    public Connection getServerToExecuteData() {
        int numberCloudConnections = cloudConnections.keySet().size();
        ServerConnectionInfo randomConnectionInfo = (ServerConnectionInfo) cloudConnections.keySet().toArray()[(int) Math.random() * numberCloudConnections];
        return cloudConnections.get(randomConnectionInfo);
    }

    public boolean isFullyConnectedToCloudServers() {
        return cloudConnectionManager.allServersAvailable();
    }

    public TaskLoadManager getTaskLoadManager() {
        return taskLoadManager;
    }

    public void shutDown() {
        cloudConnectionManager.stop();

        try {
            cloudStatusThread.join();
        } catch (InterruptedException e) {
            Logger.log(Level.ERROR, e);
        }
    }

    public String getLoadStatusString(Class taskClass) {
        StringBuilder serverStatus = new StringBuilder("Cloud Status " + taskClass.getSimpleName() + "{");
        serverStatus.append(id);
        serverStatus.append("|");
        int taskLoadForConnection = taskLoadManager.getCurrentServerLoad(taskClass);
        serverStatus.append(taskLoadForConnection);
        serverStatus.append("}");
        return serverStatus.toString();
    }

    public void printLoadStatus(Class taskClass) {
        Logger.log(Level.WARN, getLoadStatusString(taskClass));
    }

    private class CloudConnectionManager implements Runnable {

        private int threadSleepTime = 1_000;
        private int maxThreadSleepTime = 60_000;
        private boolean continueRunning = true;

        @Override
        public void run() {
            while(continueRunning) {
                checkAllCloudConnections();

                if(threadSleepTime < maxThreadSleepTime) {
                    threadSleepTime *= 2;
                }

                if(threadSleepTime > maxThreadSleepTime) {
                    threadSleepTime = maxThreadSleepTime;
                }

                try {
                    Thread.sleep(threadSleepTime);
                } catch (InterruptedException e) {
                    Logger.log(Level.ERROR, e);
                }
            }
        }

        private void checkAllCloudConnections() {
            for(ServerConnectionInfo serverConnectionInfo : cloudConnectionsToMake) {
                checkAndTryToReconnectCloudConnection(serverConnectionInfo);
            }
        }

        /**
         * Checks cloud connections for disconnected cloud connection. Reconnects if the {@link Connection} is null.
         */
        private void checkAndTryToReconnectCloudConnection(ServerConnectionInfo serverConnectionInfo) {
            Connection connection = cloudConnections.get(serverConnectionInfo);

            if(connection == null || connection.connectionShouldBeDestroyed()) {
                if(connection != null) {
                    connection.shutDownConnection();
                }

                Logger.log(Level.INFO, "Trying to connect to: " + serverConnectionInfo);
                Connection newCloudServerConnection = Connection.newClientConnection(serverConnectionInfo.getIp(), serverConnectionInfo.getPort());
                if(newCloudServerConnection != null) {
                    cloudConnections.put(serverConnectionInfo, newCloudServerConnection);
                }
            }
        }

        public boolean allServersAvailable() {
            for(ServerConnectionInfo serverConnectionInfo : cloudConnectionsToMake) {
                Connection connection = cloudConnections.get(serverConnectionInfo);

                if(connection == null || connection.connectionShouldBeDestroyed()) {
                    return false;
                }
            }

            return true;
        }

        public boolean isRunning() {
            return continueRunning;
        }

        public void stop() {
            this.continueRunning = false;
        }
    }

}
