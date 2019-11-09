package dev.fanger.simpleclients.server.data.task;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.server.data.payload.Payload;

public abstract class Task {

    public abstract void executeTask(Connection connection, Payload payload);

}
