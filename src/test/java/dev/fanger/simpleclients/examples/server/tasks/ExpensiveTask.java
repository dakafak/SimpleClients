package dev.fanger.simpleclients.examples.server.tasks;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.server.cloud.AllowCloudProcessing;
import dev.fanger.simpleclients.server.cloud.ReturnType;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;

@AllowCloudProcessing(returnType = ReturnType.GET)
//TODO update tasks to use annotations for the executed task method
//  so you can have custom task methods with custom return types and those would be checked to set the above return type
//  or restrict cloud processing to a specific return type but not exactly ideal
public class ExpensiveTask extends Task {

    @Override
    public void executeTask(Connection connection, Payload payload) {

    }

}
