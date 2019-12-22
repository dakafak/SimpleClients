package dev.fanger.simpleclients.server.handlerthreads.datahelper;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.logging.Level;
import dev.fanger.simpleclients.logging.Logger;
import dev.fanger.simpleclients.server.cloud.CloudManager;
import dev.fanger.simpleclients.server.data.payload.CloudTaskPayload;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataReceiveHelperServer extends DataReceiveHelper {

    private CloudManager cloudManager;
    private ConcurrentHashMap<Task, ConcurrentHashMap<CloudTaskProcessor, Thread>> cloudTaskProcessorsPerTasks;
    private ConcurrentHashMap<Task, ConcurrentLinkedQueue<Payload>> payloadQueuesForTasks;

    public DataReceiveHelperServer(Connection passiveConnection, ConcurrentHashMap<String, Task> tasks, CloudManager cloudManager) {
        super(passiveConnection, tasks);
        this.cloudManager = cloudManager;
        cloudTaskProcessorsPerTasks = new ConcurrentHashMap<>();
        payloadQueuesForTasks = new ConcurrentHashMap<>();
    }

    @Override
    public void sendPayloadToTaskExecution(Payload payload) {
        if(payload != null) {
            Task task = tasks.get(payload.getPayloadUrl());
            if(task != null) {
                if(task.hasAllowedCloudProcessing()) {
                    cloudManager.getTaskLoadManager().incrementServerLoad(task.getClass());

                    if(payload instanceof CloudTaskPayload) {//TODO add another check for hop count
                        addPayloadToCloudProcessors(task, payload);
                    } else if(cloudManager.shouldSendDataToAnotherServer(task.getClass(), task.getMaxLoadForCloud())) {
                        sendDataToAnotherServer(task, payload);
                        cloudManager.getTaskLoadManager().decrementServerLoad(task.getClass());
                    } else {
                        task.executeTask(passiveConnection, payload);
                        cloudManager.getTaskLoadManager().decrementServerLoad(task.getClass());
                    }

                    cloudManager.printLoadStatus(task.getClass());//TODO eventually remove this
                } else {
                    task.executeTask(passiveConnection, payload);
                }
            } else {
                Logger.log(Level.WARN, "Someone tried to connect to an invalid task, url: " + payload.getPayloadUrl());
            }
        }
    }

    private void sendDataToAnotherServer(Task task, Payload payload) {
        CloudTaskPayload cloudTaskPayload = new CloudTaskPayload(payload.getData(), payload.getPayloadUrl());

        if(task.isRequiresReturnData()) {
            Payload payloadFromHelperServer = cloudManager.getExecutedDataFromAnotherServer(cloudTaskPayload);
            passiveConnection.sendData(payloadFromHelperServer);
        } else {
            cloudManager.executePayloadOnAnotherServer(cloudTaskPayload);
        }
    }

    private void addPayloadToCloudProcessors(Task task, Payload payload) {
        if(!payloadQueuesForTasks.containsKey(task)) {
            payloadQueuesForTasks.put(task, new ConcurrentLinkedQueue<>());
        }

        if(!cloudTaskProcessorsPerTasks.containsKey(task)) {
            cloudTaskProcessorsPerTasks.put(task, new ConcurrentHashMap<>());

            for(int i = 0; i < task.getNumberThreadsForCloudTaskProcessors(); i++) {
                CloudTaskProcessor cloudTaskProcessor = new CloudTaskProcessor(task, payloadQueuesForTasks.get(task));
                Thread cloudTaskProcessorThread = new Thread(cloudTaskProcessor);
                cloudTaskProcessorsPerTasks.get(task).put(cloudTaskProcessor, cloudTaskProcessorThread);
                cloudTaskProcessorThread.start();
            }
        }

        payloadQueuesForTasks.get(task).add(payload);
    }

    public void shutdown() {
        for(ConcurrentHashMap<CloudTaskProcessor, Thread> cloudTaskProcessors : cloudTaskProcessorsPerTasks.values()) {
            for(CloudTaskProcessor cloudTaskProcessor : cloudTaskProcessors.keySet()) {
                cloudTaskProcessor.shutDown();
            }
        }
    }

    class CloudTaskProcessor implements Runnable {

        private boolean continueRunning = true;
        private Task cloudTaskProcessingType;
        private ConcurrentLinkedQueue<Payload> payloadsToExecute;

        public CloudTaskProcessor(Task cloudTaskProcessingType, ConcurrentLinkedQueue<Payload> payloadsToExecute) {
            this.cloudTaskProcessingType = cloudTaskProcessingType;
            this.payloadsToExecute = payloadsToExecute;
        }

        public void shutDown() {
            continueRunning = false;
        }

        @Override
        public void run() {
            while(continueRunning) {
                if(!payloadsToExecute.isEmpty()) {
                    Payload payloadToExecute = payloadsToExecute.poll();

                    if(payloadToExecute != null) {
                        cloudTaskProcessingType.executeTask(passiveConnection, payloadToExecute);
                        cloudManager.getTaskLoadManager().decrementServerLoad(cloudTaskProcessingType.getClass());
                    }

                    cloudManager.printLoadStatus(cloudTaskProcessingType.getClass());//TODO eventually remove this
                }
            }
        }
    }

}
