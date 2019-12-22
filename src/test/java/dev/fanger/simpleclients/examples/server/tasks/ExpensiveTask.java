package dev.fanger.simpleclients.examples.server.tasks;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.annotations.AllowCloudProcessing;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;

@AllowCloudProcessing(requiresReturnData = false, serverLoadLimit = 10)
//TODO update tasks to use annotations for the executed task method
//  so you can have custom task methods with custom return types and those would be checked to set the above return type
//  or restrict cloud processing to a specific return type but not exactly ideal
public class ExpensiveTask extends Task {

    private int id;

    public ExpensiveTask(int id) {
        this.id = id;
    }

    @Override
    public void executeTask(Connection connection, Payload payload) {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Finished running expensive task on: " + id);
    }

}
