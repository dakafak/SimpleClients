package dev.fanger.simpleclients;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.handlerthreads.datahelper.ConnectionReceiveDataHelper;

public class SimpleClient extends SimpleClientManager {

    private Connection connection;

    public SimpleClient(String hostname, int port) {
        this.connection = Connection.newConnection(hostname, port);

        ConnectionReceiveDataHelper connectionReceiveDataHelper = new ConnectionReceiveDataHelper(connection, getTasks());
        Thread connectionReceiveDataHelperThread = new Thread(connectionReceiveDataHelper);
        connectionReceiveDataHelperThread.start();
    }

    public void sendPayload(Payload payload) {
        connection.sendData(payload);
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

}
