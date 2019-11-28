package dev.fanger.simpleclients;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.handlerthreads.datahelper.ConnectionReceiveDataHelper;

import java.io.ObjectInputStream;
import java.util.UUID;

public class TraditionalClient {

    private Connection connection;

    private TraditionalClient(Builder builder) {
        connection = Connection.newClientConnection(builder.getHostname(), builder.getPort());
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
     * Having both tasks and manual data retrieval may be less performant. See
     * {@link ConnectionReceiveDataHelper#sendPayloadToTaskExecution(Payload)} for more information
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

    public static class Builder {

        private int port;
        private String hostname;

        public Builder(String hostname) {
            this.hostname = hostname;
        }

        public Builder withPort(int port) {
            this.port = port;
            return this;
        }

        public TraditionalClient build() {
            return new TraditionalClient(this);
        }

        public int getPort() {
            return port;
        }

        public String getHostname() {
            return hostname;
        }

    }

}
