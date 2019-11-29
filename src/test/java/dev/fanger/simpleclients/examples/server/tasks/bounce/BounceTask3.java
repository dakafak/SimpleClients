package dev.fanger.simpleclients.examples.server.tasks.bounce;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;

public class BounceTask3 extends Task {

    @Override
    public void executeTask(Connection connection, Payload payload) {
        System.out.println("Hit bounce task 3");
        connection.sendData(new Payload("", "/test/bounce/4"));
    }

}
