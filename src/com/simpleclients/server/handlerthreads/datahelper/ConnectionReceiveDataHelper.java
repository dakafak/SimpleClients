package com.simpleclients.server.handlerthreads.datahelper;

import com.simpleclients.connection.Connection;
import com.simpleclients.server.data.payload.Payload;
import com.simpleclients.server.data.task.Task;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionReceiveDataHelper implements Runnable {

    private boolean continueRunning;
    private Connection connection;
    private ConcurrentHashMap<Enum, Task> tasks;

    public ConnectionReceiveDataHelper(Connection connection, ConcurrentHashMap<Enum, Task> tasks) {
        this.connection = connection;
        this.tasks = tasks;
    }

    @Override
    public void run() {
        while(continueRunning){
            if(connection.clientShouldBeDestroyed()){
                continueRunning = false;
                break;
            }

            Payload newPayload = connection.retrieveData();
            if(newPayload != null) {
                if(tasks.containsKey(newPayload.getPayloadType())) {
                    tasks.get(newPayload.getPayloadType()).executeTask(connection, newPayload);
                }
            }
        }
    }

    public void setContinueRunning(boolean continueRunning) {
        this.continueRunning = continueRunning;
    }

}
