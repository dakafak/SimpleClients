package dev.fanger.simpleclients;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.handlerthreads.datahelper.ConnectionReceiveDataHelper;

import java.io.ObjectInputStream;
import java.util.UUID;

public class SimpleClient extends TaskedService {

    private Connection connection;
    private Connection passiveConnection;
    private ConnectionReceiveDataHelper connectionReceiveDataHelper;
    private Thread connectionReceiveDataHelperThread;

    public SimpleClient(String hostname, int port) {
        this.connection = Connection.newClientConnection(hostname, port);
        passiveConnection = Connection.newClientConnection(hostname, port);

        connectionReceiveDataHelper = new ConnectionReceiveDataHelper(passiveConnection, getTasks());
        connectionReceiveDataHelperThread = new Thread(connectionReceiveDataHelper);
        connectionReceiveDataHelperThread.start();
    }

    /**
     * Sends the specified {@link Payload} to the connected server
     *
     * @param payload
     */
    public void sendData(Payload payload) {
        connection.sendData(payload);
    }

    /**
     * Actively waits for a payload to be received. This will wait on the same lock as
     * {@link ObjectInputStream#readObject()}
     *
     * @return
     */
    public Payload retrieveData() {
        return connection.retrieveData();
    }

    /**
     * Shuts down this client. Passive and active {@link Connection}. This will wait on a join for the clients
     * {@link #connectionReceiveDataHelperThread} which is used for passive {@link Payload} retrieval
     * for the passive connection, {@link #passiveConnection}.
     */
    public void shutDownClient() {
        connection.shutDownConnection();
        passiveConnection.shutDownConnection();

        try {
            connectionReceiveDataHelperThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return UUID for this client
     */
    public UUID getId() {
        return connection.getId();
    }

}
