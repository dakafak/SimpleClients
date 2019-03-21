package com.dakafak.simpleclients.server.data.task;

import com.dakafak.simpleclients.connection.Connection;
import com.dakafak.simpleclients.server.data.payload.Payload;

public abstract class Task {

    public abstract void executeTask(Connection connection, Payload payload);

}
