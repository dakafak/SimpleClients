package com.simpleclients.examples;

import com.simpleclients.connection.Connection;
import com.simpleclients.server.data.payload.Payload;
import com.simpleclients.server.data.task.Task;

public class PingTask extends Task {

    @Override
    public void executeTask(Connection connection, Payload payload) {
        System.out.println("Ping request, sending payload back to " + connection.getId() + " | " + payload);
        connection.sendData(payload);
    }

}
