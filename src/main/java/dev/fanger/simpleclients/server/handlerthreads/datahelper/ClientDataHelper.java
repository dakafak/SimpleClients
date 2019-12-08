package dev.fanger.simpleclients.server.handlerthreads.datahelper;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.logging.Level;
import dev.fanger.simpleclients.logging.Logger;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;

import java.util.concurrent.ConcurrentHashMap;

public class ClientDataHelper extends ConnectionReceiveDataHelper {

    public ClientDataHelper(Connection passiveConnection, ConcurrentHashMap<String, Task> tasks) {
        super(passiveConnection, tasks);
    }

    @Override
    public void sendPayloadToTaskExecution(Payload payload) {
        if(payload != null) {
            Task task = tasks.get(payload.getPayloadUrl());
            if(task != null) {
                task.executeTask(passiveConnection, payload);
            } else {
                Logger.log(Level.WARN, "Someone tried to connect to an invalid task, url: " + payload.getPayloadUrl());
            }
        }
    }

}
