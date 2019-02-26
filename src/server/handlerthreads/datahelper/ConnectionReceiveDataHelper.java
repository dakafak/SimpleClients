package server.handlerthreads.datahelper;

import connection.Connection;
import server.data.Payload;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionReceiveDataHelper implements Runnable {

    private boolean continueRunning;
    private Connection connection;
    private ConcurrentLinkedQueue<Payload> receivedMessageQueue;

    public ConnectionReceiveDataHelper(Connection connection, ConcurrentLinkedQueue<Payload> receivedMessageQueue) {
        this.connection = connection;
        this.receivedMessageQueue = receivedMessageQueue;
    }

    @Override
    public void run() {
        while(continueRunning){
            Payload newPayload = connection.retrieveData();
            receivedMessageQueue.add(newPayload);
        }
    }

    public void setContinueRunning(boolean continueRunning) {
        this.continueRunning = continueRunning;
    }

}
