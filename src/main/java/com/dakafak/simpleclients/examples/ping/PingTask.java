package com.dakafak.simpleclients.examples.ping;

import com.dakafak.simpleclients.connection.Connection;
import com.dakafak.simpleclients.server.data.payload.Payload;
import com.dakafak.simpleclients.server.data.task.Task;

public class PingTask extends Task {

    @Override
    public void executeTask(Connection connection, Payload payload) {
        connection.sendData(payload);
    }

}
