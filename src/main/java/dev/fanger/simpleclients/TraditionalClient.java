package dev.fanger.simpleclients;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.server.data.payload.Payload;

import java.io.ObjectInputStream;
import java.util.UUID;

public class TraditionalClient {

    private Connection connection;

    public TraditionalClient(String ip, int port) {
        connection = Connection.newClientConnection(ip, port);
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
     * Shuts down this client
     */
    public void shutDownClient() {
        connection.shutDownConnection();
    }

    /**
     * @return UUID for this client
     */
    public UUID getId() {
        return connection.getId();
    }

}
