package dev.fanger.simpleclients.examples.server.tasks;

import dev.fanger.simpleclients.annotations.AdvancedTaskProperties;
import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;

@AdvancedTaskProperties(
        numberThreads = 4,
        queueCapacity = 10,
        requiresReturnData = false,
        enableCloudProcessing = true
)
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
