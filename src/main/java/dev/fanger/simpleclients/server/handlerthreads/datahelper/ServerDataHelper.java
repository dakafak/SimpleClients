package dev.fanger.simpleclients.server.handlerthreads.datahelper;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.logging.Level;
import dev.fanger.simpleclients.logging.Logger;
import dev.fanger.simpleclients.server.cloud.CloudManager;
import dev.fanger.simpleclients.server.cloud.ReturnType;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;

import java.util.concurrent.ConcurrentHashMap;

public class ServerDataHelper extends ConnectionReceiveDataHelper {

    private CloudManager cloudManager;

    public ServerDataHelper(Connection passiveConnection, ConcurrentHashMap<String, Task> tasks, CloudManager cloudManager) {
        super(passiveConnection, tasks);
        this.cloudManager = cloudManager;
    }

    @Override
    public void sendPayloadToTaskExecution(Payload payload) {
        if(payload != null) {
            Task task = tasks.get(payload.getPayloadUrl());
            if(task != null) {
                //TODO right here is probably where this should determine if the task should be sent to another server
                //  if not then continue to the below for grabbing task and running execution
                if(task.hasAllowedCloudProcessing() && cloudManager.shouldSendDataToAnotherServer()) {
                    if(ReturnType.GET.equals(task.getReturnType())) {
                        Payload payloadFromHelperServer = cloudManager.getExecutedDataFromAnotherServer(payload);
                        passiveConnection.sendData(payloadFromHelperServer);
                    } else {
                        cloudManager.executePayloadOnAnotherServer(payload);
                    }
                } else {
                    cloudManager.incrementServerLoad();
                    task.executeTask(passiveConnection, payload);
                    cloudManager.decrementServerLoad();
                }
            } else {
                Logger.log(Level.WARN, "Someone tried to connect to an invalid task, url: " + payload.getPayloadUrl());
            }


        }
    }

}
