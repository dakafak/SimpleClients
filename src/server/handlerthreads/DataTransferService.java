package server.handlerthreads;

import connection.Connection;
import connection.Id;
import server.data.payload.Payload;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataTransferService implements Runnable {

    private ConcurrentHashMap<Id, Connection> clients;
    private ConcurrentHashMap<Id, ConcurrentLinkedQueue<Payload>> outputPayloadQueuePerConnectionId;
    private boolean continueRunning;

    public DataTransferService(ConcurrentHashMap<Id, Connection> clients, ConcurrentHashMap<Id, ConcurrentLinkedQueue<Payload>> outputPayloadQueuePerConnectionId) {
        this.clients = clients;
        this.outputPayloadQueuePerConnectionId = outputPayloadQueuePerConnectionId;
    }

    @Override
    public void run() {

    }

    public void setContinueRunning(boolean continueRunning) {
        this.continueRunning = continueRunning;
    }

}
