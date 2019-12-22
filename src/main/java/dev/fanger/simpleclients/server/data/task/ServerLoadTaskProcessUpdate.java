package dev.fanger.simpleclients.server.data.task;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.logging.Level;
import dev.fanger.simpleclients.logging.Logger;
import dev.fanger.simpleclients.server.cloud.CloudManager;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.payload.ServerLoadUpdateResponse;

public class ServerLoadTaskProcessUpdate extends Task {

    private CloudManager cloudManager;

    public ServerLoadTaskProcessUpdate(CloudManager cloudManager) {
        this.cloudManager = cloudManager;
    }

    @Override
    public void executeTask(Connection connection, Payload payload) {
        if(payload.getData() instanceof ServerLoadUpdateResponse) {
            ServerLoadUpdateResponse serverLoadUpdateResponse = (ServerLoadUpdateResponse) payload.getData();
//            cloudManager.updateServerLoad(
//                    serverLoadUpdateResponse.getServerConnectionInfo(),
//                    serverLoadUpdateResponse.getTaskClass(),
//                    serverLoadUpdateResponse.getLoad());
        } else {
            Logger.log(Level.ERROR, "Wrong payload data type for updating task load");
        }
    }

}
