package dev.fanger.simpleclients.server.data.task;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.server.cloud.CloudManager;
import dev.fanger.simpleclients.server.data.payload.Payload;

public class ServerLoadTask extends Task {

    private CloudManager cloudManager;

    public ServerLoadTask(CloudManager cloudManager) {
        this.cloudManager = cloudManager;
    }

    @Override
    public void executeTask(Connection connection, Payload payload) {
        //TODO ehhh check if null payload url will be allowed, this should work for TraditionalClient type connections
        connection.sendData(new Payload(cloudManager.getCurrentServerLoad(), null));
    }

}
