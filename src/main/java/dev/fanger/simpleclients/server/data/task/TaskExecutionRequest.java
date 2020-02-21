package dev.fanger.simpleclients.server.data.task;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.server.data.payload.Payload;

public class TaskExecutionRequest {

    private Task task;
    private Connection connection;
    private Payload payload;

    public TaskExecutionRequest(Task task, Connection connection, Payload payload) {
        this.task = task;
        this.connection = connection;
        this.payload = payload;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Connection getConnection() {
        return connection;
    }

    public Payload getPayload() {
        return payload;
    }

}
