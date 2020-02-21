package dev.fanger.simpleclients.examples.server.tasks;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;

public class PingTask extends Task {

    @Override
    public void executePayload(Connection connection, Payload payload) {
        connection.sendData(payload);
    }

}
