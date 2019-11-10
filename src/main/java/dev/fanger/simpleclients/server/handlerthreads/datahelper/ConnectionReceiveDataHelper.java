package dev.fanger.simpleclients.server.handlerthreads.datahelper;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.logging.Level;
import dev.fanger.simpleclients.logging.Logger;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionReceiveDataHelper implements Runnable {

    private Connection connection;
    private ConcurrentHashMap<String, Task> tasks;

    public ConnectionReceiveDataHelper(Connection connection, ConcurrentHashMap<String, Task> tasks) {
        this.connection = connection;
        this.tasks = tasks;
    }

    @Override
    public void run() {
        while(!connection.clientShouldBeDestroyed()){
            Payload newPayload = connection.retrieveData();
            if(newPayload != null) {
                if(tasks.containsKey(newPayload.getPayloadUrl())) {
                    tasks.get(newPayload.getPayloadUrl()).executeTask(connection, newPayload);
                } else {
                    Logger.log(Level.WARN, "Someone tried to connect to an invalid task, url: " + newPayload.getPayloadUrl());
                }
            }
        }
    }

}
