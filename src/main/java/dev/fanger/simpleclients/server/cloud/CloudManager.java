package dev.fanger.simpleclients.server.cloud;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.logging.Level;
import dev.fanger.simpleclients.logging.Logger;
import dev.fanger.simpleclients.server.ServerConnectionInfo;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.payload.ServerLoadUpdateRequest;
import dev.fanger.simpleclients.server.data.payload.ServerLoadUpdateResponse;
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

    /**
     * Used to determine which server in the cloud to send the next task to
     */
    //TODO try changing class to string to use instead and use class.getsimplename
//    private ConcurrentHashMap<ServerConnectionInfo, ConcurrentHashMap<Class, Integer>> cloudConnectionLoad;
//    private ConcurrentHashMap<Class, ServerConnectionInfo> taskToServerWithSmallestLoad;

    private TaskLoadManager taskLoadManager;
    private CloudStatusManager cloudStatusManager;
    private Thread cloudStatusThread;

    public CloudManager(int id, List<ServerConnectionInfo> cloudConnectionInfo) {
        this.id = id;

        //TODO try using a separate internal SimpleServer specifically for cloud functionality that sits on a different port and only has load update tasks
        if(cloudConnectionInfo == null || cloudConnectionInfo.isEmpty()) {
            return;
        }

        cloudConnectionsToMake = new ArrayList<>(cloudConnectionInfo);
        cloudConnections = new ConcurrentHashMap<>();
//        cloudConnectionLoad = new ConcurrentHashMap<>();
//        taskToServerWithSmallestLoad = new ConcurrentHashMap<>();
    }

    public void start(Collection<Task> allTasks) {
        taskLoadManager = new TaskLoadManager(allTasks);

        cloudStatusManager = new CloudStatusManager();
        cloudStatusThread = new Thread(cloudStatusManager);

        cloudStatusThread.start();
    }

    private boolean hasServerToSendDataTo(Class taskClass) {
//        return taskToServerWithSmallestLoad.containsKey(taskClass) && taskToServerWithSmallestLoad.get(taskClass) != null;
        return true;
    }

    public void executePayloadOnAnotherServer(Class taskClass, Payload payload) {
        Connection serverToSendDataTo = getServerToExecuteData(taskClass);

        if(serverToSendDataTo != null) {
            serverToSendDataTo.sendData(payload);
        }
    }

    public Payload getExecutedDataFromAnotherServer(Class taskClass, Payload payload) {
        Connection serverToSendDataTo = getServerToExecuteData(taskClass);

        if(serverToSendDataTo != null) {
            serverToSendDataTo.sendData(payload);
            return serverToSendDataTo.retrieveData();
        }

        return null;
    }

    public boolean shouldSendDataToAnotherServer(Class taskClass, int maxLoadForTask) {
        //TODO determine if more efficient way to do the below two
        cloudStatusManager.checkAllCloudConnections();
//        cloudStatusManager.updateCloudLoadAmounts();

        if(!hasServerToSendDataTo(taskClass)) {
            return false;
        }

//        return taskLoadManager.getCurrentServerLoad(taskClass) >= maxLoadForTask &&
//                taskLoadManager.getCurrentServerLoad(taskClass) > cloudConnectionLoad.get(taskToServerWithSmallestLoad.get(taskClass)).get(taskClass);
        return taskLoadManager.getCurrentServerLoad(taskClass) >= maxLoadForTask;
    }

    public Connection getServerToExecuteData(Class taskClass) {
//        if(taskToServerWithSmallestLoad.get(taskClass) != null) {
//            return cloudConnections.get(taskToServerWithSmallestLoad.get(taskClass));
//        }
//
//        Logger.log(Level.WARN, "Could not find another server to send execution to");
//        return null;
        int numberCloudConnections = cloudConnections.keySet().size();
        ServerConnectionInfo randomConnetionInfo = (ServerConnectionInfo) cloudConnections.keySet().toArray()[(int) Math.random() * numberCloudConnections];
        return cloudConnections.get(randomConnetionInfo);
    }

    public boolean isFullyConnectedToCloudServers() {
        return cloudStatusManager.allServersAvailable();
    }

//    public boolean allServersHaveEmptyServerLoad(Class taskClass) {
//        if(taskLoadManager.getCurrentServerLoad(taskClass) > 0) {
//            return false;
//        }
//
//        try {
//            for (ServerConnectionInfo serverConnectionInfo : cloudConnectionLoad.keySet()) {
//                if (cloudConnectionLoad.get(serverConnectionInfo).get(taskClass) > 0) {
//                    return false;
//                }
//            }
//        } catch (Exception e) {
//            return false;
//        }
//
//        return true;
//    }

//    public void printCloudLoadStatus(Class taskClass) {
//        cloudStatusManager.printCloudLoadStatus(taskClass);
//    }

    public TaskLoadManager getTaskLoadManager() {
        return taskLoadManager;
    }

    public void shutDown() {
        cloudStatusManager.stop();

        try {
            cloudStatusThread.wait(1000);
        } catch (InterruptedException e) {
            Logger.log(Level.ERROR, e);
        }
    }

    public void printLoadStatus(Class taskClass) {
//            //TODO I think this needs work for grabbing the correct load status amount for neighbors, probably hitting the exception below and breaking
            StringBuilder serverStatus = new StringBuilder("Cloud Status " + taskClass.getSimpleName() + "{");
            serverStatus.append(id);
            serverStatus.append("|");

            for(int i = 0; i < cloudConnectionsToMake.size(); i++) {
                ServerConnectionInfo serverConnectionInfo = cloudConnectionsToMake.get(i);
                Connection connection = cloudConnections.get(serverConnectionInfo);
                if(connection != null && !connection.connectionShouldBeDestroyed()) {
                    try {
//                        Integer taskLoadForConnection = cloudConnectionLoad.get(serverConnectionInfo).get(taskClass);
                        int taskLoadForConnection = taskLoadManager.getCurrentServerLoad(taskClass);

                        serverStatus.append(taskLoadForConnection);
                    } catch (Exception e) {
                        serverStatus.append("0");
                    }
                } else {
                    serverStatus.append("X");
                }

                if(i < cloudConnectionsToMake.size() - 1) {
                    serverStatus.append(",");
                }
            }
            serverStatus.append("}");
            Logger.log(Level.WARN, serverStatus.toString());
        }

//    public void updateLoadFromRemoteServer(ServerConnectionInfo serverConnectionInfo,
//                                           ConcurrentHashMap<Class, TaskLoad> taskLoadFromOtherServer) {
//        cloudConnectionLoad.put(serverConnectionInfo, taskLoadFromOtherServer);
//    }

//    public void updateServerLoad(ServerConnectionInfo serverConnectionInfo, Class taskClass, Integer load) {
//        if(!cloudConnectionLoad.containsKey(serverConnectionInfo)) {
//            cloudConnectionLoad.put(serverConnectionInfo, new ConcurrentHashMap<>());
//        }
//        cloudConnectionLoad.get(serverConnectionInfo).put(taskClass, load);
//    }

    private class CloudStatusManager implements Runnable {

        private int threadSleepTime = 1_000;
        private boolean continueRunning = true;

        @Override
        public void run() {
            while(continueRunning) {
                checkAllCloudConnections();
//                updateCloudLoadAmounts();
//                printAllCloudStatus();

                try {
                    Thread.sleep(threadSleepTime);
                } catch (InterruptedException e) {
                    Logger.log(Level.ERROR, e);
                }
            }
        }

        //TODO update this to send current task load and update task to input that
//        private void updateCloudLoadAmounts() {
//            for(ServerConnectionInfo serverConnectionInfo : cloudConnections.keySet()) {
//                for(Class taskClass : taskLoadManager.getTaskClassList()) {
//                    Connection serverConnection = cloudConnections.get(serverConnectionInfo);
//
//                    if (serverConnection != null && !serverConnection.connectionShouldBeDestroyed()) {
//                        ServerLoadUpdateRequest serverLoadUpdateRequest = new ServerLoadUpdateRequest(taskClass, serverConnectionInfo);
//                        Payload serverLoadRequest = new Payload(serverLoadUpdateRequest, "/system/server/load");
//                        serverConnection.sendData(serverLoadRequest);
//
//                        Payload response = serverConnection.retrieveData();
//                        if(response != null ) {
//                            ServerLoadUpdateResponse serverLoadUpdateResponse = (ServerLoadUpdateResponse) response.getData();
//                            updateServerLoad(
//                                    serverLoadUpdateResponse.getServerConnectionInfo(),
//                                    serverLoadUpdateResponse.getTaskClass(),
//                                    serverLoadUpdateResponse.getLoad());
//
////                            System.out.println("Updated server load from cloud manager");
////                            System.out.println(response);
//                        } else {
//                            Logger.log(Level.ERROR, "Server load response was null");
//                        }
//
////                        Payload serverLoadResponse = serverConnection.retrieveData();
////
////                        if (serverLoadResponse != null) {
////                            if (serverLoadResponse.getData() instanceof Integer) {
////                                if(!cloudConnectionLoad.containsKey(serverConnectionInfo)) {
////                                    cloudConnectionLoad.put(serverConnectionInfo, new ConcurrentHashMap<>());
////                                }
////                                cloudConnectionLoad.get(serverConnectionInfo).put(taskClass, (Integer) serverLoadResponse.getData());
//////                            ArrayList<ServerLoadTaskRequestUpdate.ClassTaskLoadPair> allTaskLoadPairs = (ArrayList<ServerLoadTaskRequestUpdate.ClassTaskLoadPair>) serverLoadResponse.getData();
//////                            ConcurrentHashMap<Class, TaskLoad> taskLoadFromOtherServer = (ConcurrentHashMap<Class, TaskLoad>) serverLoadResponse.getData();
//////                            cloudConnectionLoad.put(serverConnectionInfo, taskLoadFromOtherServer);
//////                            for(ServerLoadTaskRequestUpdate.ClassTaskLoadPair classTaskLoadPair : allTaskLoadPairs) {
//////                                cloudConnectionLoad.get(serverConnectionInfo).put(classTaskLoadPair.getTaskClass(), classTaskLoadPair.getTaskLoad());
//////                            }
////
////                            } else {
////                                Logger.log(Level.ERROR, "Server load returned a wrong value type: " + serverLoadResponse.getData());
////                            }
////                        } else {
////                            Logger.log(Level.ERROR, "Server load response was null");
////                        }
//                    } else {
//                        Logger.log(Level.ERROR, "Server connection is null: " + serverConnectionInfo);
//                    }
//                }
//            }
//
//            for(Class taskClassToSort : taskLoadManager.getTaskClassList()) {
//                List<ServerConnectionInfo> sortedConnections = new ArrayList<>();
//
//                for(ServerConnectionInfo serverConnectionInfo : cloudConnectionLoad.keySet()) {
//                    if(cloudConnectionLoad.get(serverConnectionInfo).containsKey(taskClassToSort)) {
//                        sortedConnections.add(serverConnectionInfo);
//                    }
//                }
//
//                sortedConnections.sort((o1, o2) -> {//TODO holy shit clean this up
//                    if (cloudConnectionLoad.get(o1).get(taskClassToSort) < cloudConnectionLoad.get(o2).get(taskClassToSort)) {
//                        return -1;
//                    } else {
//                        return 1;
//                    }
//                });
//
//                if (!sortedConnections.isEmpty()) {
//                    taskToServerWithSmallestLoad.put(taskClassToSort, sortedConnections.get(0));
//                }
//            }
//
//            printAllCloudStatus();
//        }

//        private void printAllCloudStatus() {
//            for(ServerConnectionInfo serverConnectionInfo : cloudConnectionLoad.keySet()) {
//                for(Class taskClass : cloudConnectionLoad.get(serverConnectionInfo).keySet()) {
//                    printCloudLoadStatus(taskClass);
//                }
//            }
//        }

//        private void printCloudLoadStatus(Class taskClass) {
//            //TODO I think this needs work for grabbing the correct load status amount for neighbors, probably hitting the exception below and breaking
//            StringBuilder serverStatus = new StringBuilder("Cloud Status " + taskClass.getSimpleName() + "{");
//            serverStatus.append(taskLoadManager.getCurrentServerLoad(taskClass));
//            serverStatus.append("|");
//
//            for(int i = 0; i < cloudConnectionsToMake.size(); i++) {
//                ServerConnectionInfo serverConnectionInfo = cloudConnectionsToMake.get(i);
//                Connection connection = cloudConnections.get(serverConnectionInfo);
//                if(connection != null && !connection.connectionShouldBeDestroyed()) {
//                    try {
//                        Integer taskLoadForConnection = cloudConnectionLoad.get(serverConnectionInfo).get(taskClass);
//
//                        serverStatus.append(taskLoadForConnection);
//                    } catch (Exception e) {
//                        serverStatus.append("0");
//                    }
//                } else {
//                    serverStatus.append("X");
//                }
//
//                if(i < cloudConnectionsToMake.size() - 1) {
//                    serverStatus.append(",");
//                }
//            }
//            serverStatus.append("}");
//            Logger.log(Level.WARN, serverStatus.toString());
//        }

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
