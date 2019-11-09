package dev.fanger.simpleclients.client;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;
import dev.fanger.simpleclients.server.handlerthreads.datahelper.ConnectionReceiveDataHelper;

import java.util.concurrent.ConcurrentHashMap;

public class SimpleClient {

    private Connection connection;
    private ConcurrentHashMap<Enum, Task> tasks;

    public SimpleClient(String hostname, int port) {
        this.connection = Connection.newConnection(hostname, port);
        this.tasks = new ConcurrentHashMap<>();

        ConnectionReceiveDataHelper connectionReceiveDataHelper = new ConnectionReceiveDataHelper(connection, tasks);
        Thread connectionReceiveDataHelperThread = new Thread(connectionReceiveDataHelper);
        connectionReceiveDataHelperThread.start();
    }

    public void addTask(Enum taskType, Task task){
        tasks.put(taskType, task);
    }

    public void sendPayload(Payload payload) {
        connection.sendData(payload);
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public ConcurrentHashMap<Enum, Task> getTasks() {
        return tasks;
    }

    public void setTasks(ConcurrentHashMap<Enum, Task> tasks) {
        this.tasks = tasks;
    }

}
