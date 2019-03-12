package com.simpleclients.server.data.task;

import com.simpleclients.connection.Connection;
import com.simpleclients.server.data.payload.Payload;

public abstract class Task {

    public abstract void executeTask(Connection connection, Payload payload);

}
