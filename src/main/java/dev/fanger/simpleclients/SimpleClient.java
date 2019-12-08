package dev.fanger.simpleclients;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.server.ServerConnectionInfo;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;
import dev.fanger.simpleclients.server.handlerthreads.datahelper.ClientDataHelper;
import dev.fanger.simpleclients.server.handlerthreads.datahelper.ConnectionReceiveDataHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class SimpleClient extends TaskedService {

    private Connection connection;
    private ConnectionReceiveDataHelper connectionReceiveDataHelper;
    private Thread connectionReceiveDataHelperThread;

    private SimpleClient(Builder builder) {
        super(builder.getTasks());
        connection = Connection.newClientConnection(builder.getServerConnectionInfo().getIp(), builder.getServerConnectionInfo().getPort());

        connectionReceiveDataHelper = new ClientDataHelper(connection, getTasks());
        connectionReceiveDataHelperThread = new Thread(connectionReceiveDataHelper);
        connectionReceiveDataHelperThread.start();
    }

    /**
     * Sends the specified {@link Payload} to the connected server
     *
     * @param payload
     */
    public void sendData(Payload payload) {
        connection.sendData(payload);
    }

    /**
     * Shuts down this client. This will wait on a join for the clients {@link #connectionReceiveDataHelperThread}
     * which is used for passive {@link Payload} retrieval
     */
    public void shutDownClient() {
        connection.shutDownConnection();

        try {
            connectionReceiveDataHelperThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return UUID for this client
     */
    public UUID getId() {
        return connection.getId();
    }

    public static class Builder {

        private List<Task> tasks;
        private ServerConnectionInfo serverConnectionInfo;

        public Builder(String ip, int port) {
            this.serverConnectionInfo = new ServerConnectionInfo(ip, port);
            tasks = new LinkedList<>();
        }

        public Builder withTask(String taskUrl, Task task) {
            task.setUrl(taskUrl);
            tasks.add(task);
            return this;
        }

        public SimpleClient build() {
            return new SimpleClient(this);
        }

        public ServerConnectionInfo getServerConnectionInfo() {
            return serverConnectionInfo;
        }

        public List<Task> getTasks() {
            return tasks;
        }

    }

}
