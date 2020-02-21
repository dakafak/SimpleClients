package dev.fanger.simpleclients.examples.client.tasks.bounce;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;

public class BounceTask4 extends Task {

    private boolean[] completedBounceTest;

    public BounceTask4(boolean[] completedBounceTest) {
        this.completedBounceTest = completedBounceTest;
    }

    @Override
    public void executePayload(Connection connection, Payload payload) {
        System.out.println("Hit bounce task 4");
        completedBounceTest[0] = true;
    }
}
