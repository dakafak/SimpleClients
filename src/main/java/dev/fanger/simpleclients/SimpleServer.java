package dev.fanger.simpleclients;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.exceptions.TaskExistsException;
import dev.fanger.simpleclients.logging.Logger;
import dev.fanger.simpleclients.server.ServerConnectionInfo;
import dev.fanger.simpleclients.server.cloud.CloudManager;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;
import dev.fanger.simpleclients.server.handlerthreads.ConnectionService;
import dev.fanger.simpleclients.server.handlerthreads.datahelper.ConnectionReceiveDataHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleServer extends TaskedService {

    private ConnectionService connectionService;

    private ConcurrentHashMap<UUID, Connection> clients;
    private ConcurrentHashMap<UUID, ConnectionReceiveDataHelper> connectionReceiveDataHelpers;
    private int port;
    private CloudManager cloudManager;

    private SimpleServer(Builder builder) {
        super(builder.getTasks());
        this.port = builder.getPort();
        clients = new ConcurrentHashMap<>();
        connectionReceiveDataHelpers = new ConcurrentHashMap<>();

        overrideLoggerType(builder.getLoggerClassType());

        this.cloudManager = new CloudManager(builder.getCloudConnectionInfo());
    }

    /**
     * Starts the connection service thread. The connection service thread will actively listen to connections and
     * immediately add them to the clients {@link ConcurrentHashMap} and setup {@link ConnectionReceiveDataHelper}
     * for each client.
     */
    public void startListeningForConnections(){
        connectionService = new ConnectionService(port, clients, connectionReceiveDataHelpers, getTasks(), cloudManager);
        Thread connectionThread = new Thread(connectionService);
        connectionThread.start();
    }

    /**
     * Shuts down the {@link ConnectionService} for this server
     */
    public void shutDownServer(){
        connectionService.shutdown();
    }

    /**
     * Sends a payload to a specified client {@link Connection}
     *
     * @param connection
     * @param payload
     * @return
     */
    public boolean sendPayloadToClient(Connection connection, Payload payload){
        if(!connection.connectionShouldBeDestroyed()){
            connection.sendData(payload);
            return true;
        }

        return false;
    }

    /**
     * @param id
     * @return {@link Connection} for the specified UUID
     */
    public Connection getClient(UUID id){
        if(id != null && clients.containsKey(id)) {
            return clients.get(id);
        }

        return null;
    }

    /**
     * Overrides the SimpleClients {@link Logger} with the specified type
     *
     * @param loggerClassType
     */
    private void overrideLoggerType(Class<? extends Logger> loggerClassType) {
        Logger.overrideLoggerType(loggerClassType);
    }

    public ConcurrentHashMap<UUID, Connection> getClients() {
        return clients;
    }

    public int getPort() {
        return port;
    }

    public int getCurrentServerLoad() {
        return cloudManager.getCurrentServerLoad();
    }

    public static class Builder {

        private int port;
        private Class<? extends Logger> loggerClassType;
        private List<Task> tasks;
        private List<ServerConnectionInfo> cloudConnectionInfo;

        public Builder(int port) {
            this.port = port;
            tasks = new LinkedList<>();
            //TODO put default built in tasks here
            cloudConnectionInfo = new LinkedList<>();
        }

        public Builder withLoggingType(Class<? extends Logger> loggerClassType) {
            this.loggerClassType = loggerClassType;

            return this;
        }

        /**
         * Throws {@link TaskExistsException} if the desired task url is already being used by an existing task
         * Primarily used to block overriding of built in tasks
         *
         * @param taskUrl
         * @param task
         * @return
         * @throws TaskExistsException
         */
        public Builder withTask(String taskUrl, Task task) throws TaskExistsException {
            if(tasks.contains(taskUrl)) {
                throw new TaskExistsException();
            }

            task.setUrl(taskUrl);
            tasks.add(task);

            return this;
        }

        public Builder withCloudConnectionInfo(ServerConnectionInfo... serverConnectionInfos) {
            if(serverConnectionInfos != null) {
                for(ServerConnectionInfo serverConnectionInfo : serverConnectionInfos) {
                    cloudConnectionInfo.add(serverConnectionInfo);
                }
            }

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

        public List<ServerConnectionInfo> getCloudConnectionInfo() {
            return cloudConnectionInfo;
        }

    }

}
