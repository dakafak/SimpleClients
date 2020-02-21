package dev.fanger.simpleclients.examples.server.tasks;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.annotations.AllowCloudProcessing;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;

@AllowCloudProcessing(
        requiresReturnData = false,
        serverLoadLimit = 10,
        numberCloudTaskProcessingThreads = 4)
public class ExpensiveTask extends Task {

    private int id;

    public ExpensiveTask(int id) {
        this.id = id;
    }

    @Override
    public void executePayload(Connection connection, Payload payload) {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(payload.getData().equals("Expensive")) {
            System.out.println("Finished running expensive task on: " + id);
        } else {
            System.out.println("Bad data, failed to run expensive task on: " + id);
        }
    }

}
