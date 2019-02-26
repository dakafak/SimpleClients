package server.handlerthreads;

import connection.Connection;
import connection.Id;
import server.data.Payload;
import server.handlerthreads.datahelper.ConnectionReceiveDataHelper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionValidatorService implements Runnable {

    private boolean continueRunning;

    private ConcurrentHashMap<Id, Connection> clients;
    private ConcurrentLinkedQueue<Connection> clientsToValidate;
    private ConcurrentHashMap<Id, ConnectionReceiveDataHelper> connectionReceiveDataHelpers;
    private ConcurrentHashMap<Id, ConcurrentLinkedQueue<Payload>> inputPayloadQueuePerConnectionId;

    public ConnectionValidatorService(ConcurrentHashMap<Id, Connection> clients,
                                      ConcurrentLinkedQueue<Connection> clientsToValidate,
                                      ConcurrentHashMap<Id, ConnectionReceiveDataHelper> connectionReceiveDataHelpers,
                                      ConcurrentHashMap<Id, ConcurrentLinkedQueue<Payload>> inputPayloadQueuePerConnectionId) {
        this.clients = clients;
        this.clientsToValidate = clientsToValidate;
        this.connectionReceiveDataHelpers = connectionReceiveDataHelpers;
        this.inputPayloadQueuePerConnectionId = inputPayloadQueuePerConnectionId;
    }

    @Override
    public void run() {
        while(continueRunning) {
            if(!clientsToValidate.isEmpty()) {
                Connection newClientConnection = clientsToValidate.poll();

                //TODO when correct validation is added, wrap these with a validation check
                newClientConnection.setId(new Id((long)Math.random()*100));
                clients.put(newClientConnection.getId(), newClientConnection);
                ConcurrentLinkedQueue<Payload> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
                inputPayloadQueuePerConnectionId.put(newClientConnection.getId(), concurrentLinkedQueue);
                ConnectionReceiveDataHelper connectionReceiveDataHelper = new ConnectionReceiveDataHelper(newClientConnection, concurrentLinkedQueue);
                connectionReceiveDataHelper.setContinueRunning(true);
                Thread connectionReceiveDataHelperThread = new Thread(connectionReceiveDataHelper);
                connectionReceiveDataHelperThread.start();
                connectionReceiveDataHelpers.put(newClientConnection.getId(), connectionReceiveDataHelper);
            }
        }
    }

    public void setContinueRunning(boolean continueRunning) {
        this.continueRunning = continueRunning;
    }

}
