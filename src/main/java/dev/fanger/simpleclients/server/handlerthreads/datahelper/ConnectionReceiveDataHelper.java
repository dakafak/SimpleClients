package dev.fanger.simpleclients.server.handlerthreads.datahelper;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;

import java.util.concurrent.ConcurrentHashMap;

public abstract class ConnectionReceiveDataHelper implements Runnable {

    protected Connection passiveConnection;
    protected ConcurrentHashMap<String, Task> tasks;

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
     *
     * @param payload
     */
    public abstract void sendPayloadToTaskExecution(Payload payload);

    public boolean canExecutePayload(Payload payload) {
        return payload != null &&
                payload.getPayloadUrl() != null &&
                tasks.containsKey(payload.getPayloadUrl());
    }

}
