package dev.fanger.simpleclients.server.data.task;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.server.data.payload.Payload;

public abstract class Task {

    private String url;

    public Task(String url) {
        this.url = url;
    }

    public abstract void executeTask(Connection connection, Payload payload);

    public String getUrl() {
        return url;
    }

}
