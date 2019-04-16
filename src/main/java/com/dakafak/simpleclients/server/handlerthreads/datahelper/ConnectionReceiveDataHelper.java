package com.dakafak.simpleclients.server.handlerthreads.datahelper;

import com.dakafak.simpleclients.connection.Connection;
import com.dakafak.simpleclients.server.data.payload.Payload;
import com.dakafak.simpleclients.server.data.task.Task;
import com.dakafak.simpleclients.server.data.task.type.DefaultTaskTypes;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionReceiveDataHelper implements Runnable {

    private Connection connection;
    private ConcurrentHashMap<Enum, Task> tasks;

    public ConnectionReceiveDataHelper(Connection connection, ConcurrentHashMap<Enum, Task> tasks) {
        this.connection = connection;
        this.tasks = tasks;
    }

    @Override
    public void run() {
        while(!connection.clientShouldBeDestroyed()){
            Payload newPayload = connection.retrieveData();
            if(newPayload != null) {
                if(tasks.containsKey(newPayload.getPayloadType())) {
                    tasks.get(newPayload.getPayloadType()).executeTask(connection, newPayload);
                }
            }
        }

        tasks.get(DefaultTaskTypes.DISCONNECT_CLIENT).executeTask(connection, null);
    }

}
