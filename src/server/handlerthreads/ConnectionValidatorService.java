package server.handlerthreads;

import connection.Connection;
import connection.Id;
import server.data.datatypes.ConnectionPayloadPair;
import server.data.payload.Payload;
import server.handlerthreads.datahelper.ConnectionReceiveDataHelper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionValidatorService implements Runnable {

    private boolean continueRunning;

    private ConcurrentHashMap<Id, Connection> clients;
    private ConcurrentLinkedQueue<Connection> clientsToValidate;
    private ConcurrentHashMap<Id, ConnectionReceiveDataHelper> connectionReceiveDataHelpers;
    private ConcurrentLinkedQueue<ConnectionPayloadPair> inputQueue;
    private TaskExecutorService taskExecutorService;

    public ConnectionValidatorService(ConcurrentHashMap<Id, Connection> clients,
                                      ConcurrentLinkedQueue<Connection> clientsToValidate,
                                      ConcurrentHashMap<Id, ConnectionReceiveDataHelper> connectionReceiveDataHelpers,
                                      ConcurrentLinkedQueue<ConnectionPayloadPair> inputQueue,
                                      TaskExecutorService taskExecutorService) {
        this.clients = clients;
        this.clientsToValidate = clientsToValidate;
        this.connectionReceiveDataHelpers = connectionReceiveDataHelpers;
        this.inputQueue = inputQueue;
        this.taskExecutorService = taskExecutorService;
    }

    @Override
    public void run() {
        while(continueRunning) {
            if(!clientsToValidate.isEmpty()) {
                Connection newClientConnection = clientsToValidate.poll();

                //TODO when correct validation is added, wrap these with a validation check
                newClientConnection.setId(new Id((long)Math.random()*100));
                clients.put(newClientConnection.getId(), newClientConnection);

                ConnectionReceiveDataHelper connectionReceiveDataHelper = new ConnectionReceiveDataHelper(newClientConnection, inputQueue, taskExecutorService);
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
