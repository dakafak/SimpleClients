package server.handlerthreads.datahelper;

import connection.Connection;
import server.data.datatypes.ConnectionPayloadPair;
import server.data.payload.Payload;
import server.handlerthreads.TaskExecutorService;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionReceiveDataHelper implements Runnable {

    private boolean continueRunning;
    private Connection connection;
    private ConcurrentLinkedQueue<ConnectionPayloadPair> receivedMessageQueue;
    private TaskExecutorService taskExecutorService;

    public ConnectionReceiveDataHelper(Connection connection, ConcurrentLinkedQueue<ConnectionPayloadPair> inputQueue, TaskExecutorService taskExecutorService) {
        this.connection = connection;
        this.receivedMessageQueue = inputQueue;
        this.taskExecutorService = taskExecutorService;
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
                if(taskExecutorService.containsTaskType(newPayload.getPayloadType())) {
                    taskExecutorService.addTaskToExecute(connection, newPayload);
                } else {
                    receivedMessageQueue.add(new ConnectionPayloadPair(connection, newPayload));
                }
            }
        }
    }

    public void setContinueRunning(boolean continueRunning) {
        this.continueRunning = continueRunning;
    }

}
