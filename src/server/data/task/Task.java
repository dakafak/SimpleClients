package server.data.task;

import connection.Connection;
import server.data.payload.Payload;

public abstract class Task {

    public abstract void executeTask(Connection connection, Payload payload);

}
