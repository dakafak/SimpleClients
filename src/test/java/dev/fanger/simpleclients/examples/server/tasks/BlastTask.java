package dev.fanger.simpleclients.examples.server.tasks;

import dev.fanger.simpleclients.annotations.AdvancedTaskProperties;
import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;

@AdvancedTaskProperties(
        numberThreads = 2,
        queueCapacity = 16
)
public class BlastTask extends Task {

    @Override
    protected void executePayload(Connection connection, Payload payload) {
        // Nope
    }

}
