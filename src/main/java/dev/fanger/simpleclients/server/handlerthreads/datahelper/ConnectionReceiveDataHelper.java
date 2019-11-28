package dev.fanger.simpleclients.server.handlerthreads.datahelper;

import dev.fanger.simpleclients.SimpleClient;
import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.logging.Level;
import dev.fanger.simpleclients.logging.Logger;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionReceiveDataHelper implements Runnable {

    private Connection passiveConnection;
    private ConcurrentHashMap<String, Task> tasks;

    public ConnectionReceiveDataHelper(Connection passiveConnection, ConcurrentHashMap<String, Task> tasks) {
        this.passiveConnection = passiveConnection;
        this.tasks = tasks;
    }

    @Override
    public void run() {
        while(!passiveConnection.connectionShouldBeDestroyed()){
            Payload newPayload = passiveConnection.retrieveData();
            if(canExecutePayload(newPayload)) {
                sendPayloadToTaskExecution(newPayload);
            }
        }
    }

    /**
     * Attempts to send a specified payload to task execution.
     * This is public so that {@link SimpleClient} can attempt to fix payloads incorrectly retrieved by
     * {@link SimpleClient#retrieveData()}
     * Doing so is not as performant because the task will be executed on the primary thread so having a client that
     * both handles tasks as well as manually waits for data is not ideal
     *
     * @param payload
     */
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

    public boolean canExecutePayload(Payload payload) {
        return payload != null &&
                payload.getPayloadUrl() != null &&
                tasks.containsKey(payload.getPayloadUrl());
    }

}
