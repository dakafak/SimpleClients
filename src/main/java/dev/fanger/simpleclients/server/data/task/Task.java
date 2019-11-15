package dev.fanger.simpleclients.server.data.task;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.handlerthreads.datahelper.ConnectionReceiveDataHelper;

public abstract class Task {

    private String url;

    public Task(String url) {
        this.url = url;
    }

    /**
     * When a payload is retrieved by {@link ConnectionReceiveDataHelper} it will call this method immediately
     *
     * @param connection
     * @param payload
     */
    public abstract void executeTask(Connection connection, Payload payload);

    public String getUrl() {
        return url;
    }

}
