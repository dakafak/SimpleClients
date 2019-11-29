package dev.fanger.simpleclients;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;
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
        connection = Connection.newClientConnection(builder.getHostname(), builder.getPort());

        connectionReceiveDataHelper = new ConnectionReceiveDataHelper(connection, getTasks());
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

        private int port;
        private String hostname;
        private List<Task> tasks;

        public Builder(String hostname) {
            this.hostname = hostname;
            tasks = new LinkedList<>();
        }

        public Builder withPort(int port) {
            this.port = port;
            return this;
        }

        public Builder withTask(String taskUrl, Task task) {
            task.setUrl(taskUrl);
            tasks.add(task);
            return this;
        }

        public SimpleClient build() {
            return new SimpleClient(this);
        }

        public int getPort() {
            return port;
        }

        public String getHostname() {
            return hostname;
        }

        public List<Task> getTasks() {
            return tasks;
        }
    }

}
