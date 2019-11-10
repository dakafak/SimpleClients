package dev.fanger.simpleclients.examples.Tasks;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;

public class PingTask extends Task {

    public PingTask(String url) {
        super(url);
    }

    @Override
    public void executeTask(Connection connection, Payload payload) {
        connection.sendData(payload);
    }

}
