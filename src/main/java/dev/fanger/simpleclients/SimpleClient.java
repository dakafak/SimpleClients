package dev.fanger.simpleclients;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.exceptions.DuplicateTaskException;
import dev.fanger.simpleclients.server.ServerConnectionInfo;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;
import dev.fanger.simpleclients.server.handlerthreads.datahelper.DataReceiveHelperClient;
import dev.fanger.simpleclients.server.handlerthreads.datahelper.DataReceiveHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class SimpleClient extends TaskedService {

    private Connection connection;
    private DataReceiveHelper dataReceiveHelper;
    private Thread dataReceiveHelperThread;

    private SimpleClient(Builder builder) throws DuplicateTaskException {
        super(builder.getTasks());
        connection = Connection.newClientConnection(builder.getServerConnectionInfo().getIp(), builder.getServerConnectionInfo().getPort());

        dataReceiveHelper = new DataReceiveHelperClient(connection, getTasks());
        dataReceiveHelperThread = new Thread(dataReceiveHelper);
        dataReceiveHelperThread.start();
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
     * Shuts down this client. This will wait on a join for the clients {@link #dataReceiveHelperThread}
     * which is used for passive {@link Payload} retrieval
     */
    public void shutDownClient() {
        connection.shutDownConnection();

        try {
            dataReceiveHelperThread.join();
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

        public SimpleClient build() throws DuplicateTaskException {
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
