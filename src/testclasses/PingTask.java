package testclasses;

import connection.Connection;
import server.data.payload.Payload;
import server.data.task.Task;

public class PingTask extends Task {

    @Override
    public void executeTask(Connection connection, Payload payload) {
        System.out.println("Ping request, sending payload back to " + connection.getId());
        connection.sendData(payload);
    }

}
