package server.handlerthreads;

import connection.Connection;
import connection.Id;
import server.data.Payload;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataReceiveService implements Runnable {

    private ConcurrentHashMap<Id, Connection> clients;
    private ConcurrentHashMap<Id, ConcurrentLinkedQueue<Payload>> inputPayloadQueuePerConnectionId;
    private boolean continueRunning;

    public DataReceiveService(ConcurrentHashMap<Id, Connection> clients, ConcurrentHashMap<Id, ConcurrentLinkedQueue<Payload>> inputPayloadQueuePerConnectionId) {
        this.clients = clients;
        this.inputPayloadQueuePerConnectionId = inputPayloadQueuePerConnectionId;
    }

    @Override
    public void run() {

    }

    public void setContinueRunning(boolean continueRunning) {
        this.continueRunning = continueRunning;
    }

}
