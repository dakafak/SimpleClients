package dev.fanger.simpleclients;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.exceptions.DuplicateTaskException;
import dev.fanger.simpleclients.logging.Logger;
import dev.fanger.simpleclients.server.ServerConnectionInfo;
import dev.fanger.simpleclients.server.cloud.CloudManager;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;
import dev.fanger.simpleclients.server.handlerthreads.ConnectionService;
import dev.fanger.simpleclients.server.handlerthreads.datahelper.DataReceiveHelper;
import dev.fanger.simpleclients.server.handlerthreads.datahelper.DataReceiveHelperServer;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleServer extends TaskedService {

    private ConnectionService connectionService;
    private Thread connectionServiceThread;

    private ConcurrentHashMap<UUID, Connection> clients;
    private ConcurrentHashMap<UUID, DataReceiveHelperServer> dataReceiveHelpers;
    private int port;
    private CloudManager cloudManager;

    private SimpleServer(Builder builder) throws DuplicateTaskException {
        super(builder.getTasks());
        this.port = builder.getPort();
        clients = new ConcurrentHashMap<>();
        dataReceiveHelpers = new ConcurrentHashMap<>();

        overrideLoggerType(builder.getLoggerClassType());

        // Setup cloud manager
        cloudManager = new CloudManager(port, builder.getCloudConnectionInfo());

        // Start cloud manager with list of all tasks
        cloudManager.start(getTasks().values());
    }

    /**
     * Starts the connection service thread. The connection service thread will actively listen to connections and
     * immediately add them to the clients {@link ConcurrentHashMap} and setup {@link DataReceiveHelper}
     * for each client.
     */
    public void startListeningForConnections(){
        connectionService = new ConnectionService(port, clients, dataReceiveHelpers, getTasks(), cloudManager);
        connectionServiceThread = new Thread(connectionService);
        connectionServiceThread.start();
    }

    /**
     * Shuts down the {@link ConnectionService} for this server
     */
    public void shutDownServer(){
        cloudManager.shutDown();
        connectionServiceThread.interrupt();
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

    public CloudManager getCloudManager() {
        return cloudManager;
    }

    public ConcurrentHashMap<UUID, Connection> getClients() {
        return clients;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "SimpleServer{" +
                "connectionService=" + connectionService +
                ", clients=" + clients +
                ", dataReceiveHelpers=" + dataReceiveHelpers +
                ", port=" + port +
                ", cloudManager=" + cloudManager +
                '}';
    }

    public static class Builder {

        private int port;
        private Class<? extends Logger> loggerClassType;
        private List<Task> tasks;
        private List<ServerConnectionInfo> cloudConnectionInfo;

        public Builder(int port) {
            this.port = port;
            tasks = new LinkedList<>();
            cloudConnectionInfo = new LinkedList<>();
        }

        public Builder withLoggingType(Class<? extends Logger> loggerClassType) {
            this.loggerClassType = loggerClassType;

            return this;
        }

        public Builder withTask(String taskUrl, Task task) {
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

        public SimpleServer build() throws DuplicateTaskException {
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
