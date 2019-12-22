package dev.fanger.simpleclients.server.handlerthreads.datahelper;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.logging.Level;
import dev.fanger.simpleclients.logging.Logger;
import dev.fanger.simpleclients.server.cloud.CloudManager;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;

import java.util.concurrent.ConcurrentHashMap;

public class DataReceiveHelperServer extends DataReceiveHelper {

    private CloudManager cloudManager;

    public DataReceiveHelperServer(Connection passiveConnection, ConcurrentHashMap<String, Task> tasks, CloudManager cloudManager) {
        super(passiveConnection, tasks);
        this.cloudManager = cloudManager;
    }

    @Override
    public void sendPayloadToTaskExecution(Payload payload) {
        if(payload != null) {
            Task task = tasks.get(payload.getPayloadUrl());
            if(task != null) {
                if(task.hasAllowedCloudProcessing()) {
                    cloudManager.printLoadStatus(task.getClass());
                    cloudManager.getTaskLoadManager().incrementServerLoad(task.getClass());

                    if(cloudManager.shouldSendDataToAnotherServer(task.getClass(), task.getMaxLoadForCloud())) {
                        if(task.isRequiresReturnData()) {
                            Payload payloadFromHelperServer = cloudManager.getExecutedDataFromAnotherServer(task.getClass(), payload);
                            passiveConnection.sendData(payloadFromHelperServer);
                        } else {
                            cloudManager.executePayloadOnAnotherServer(task.getClass(), payload);
                        }
                    } else {
                        task.executeTask(passiveConnection, payload);
                    }

                    cloudManager.getTaskLoadManager().decrementServerLoad(task.getClass());
                } else {
                    task.executeTask(passiveConnection, payload);
                }
            } else {
                Logger.log(Level.WARN, "Someone tried to connect to an invalid task, url: " + payload.getPayloadUrl());
            }
        }
    }

}
