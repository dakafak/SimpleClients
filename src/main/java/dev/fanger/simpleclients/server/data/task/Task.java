package dev.fanger.simpleclients.server.data.task;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.handlerthreads.datahelper.DataReceiveHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public abstract class Task {

    private String url;
    private boolean enableCloudProcessing;
    private boolean requiresReturnData;
    private int queueCapacity = 128;
    private int numberThreads = 1;
    private ArrayBlockingQueue<TaskExecutionRequest> taskExecutionRequests;
    private List<TaskExecutor> taskExecutors;
    private List<Thread> taskExecutorThreads;

    /**
     * Must be implemented by Tasks, executePayload is called by {@link TaskExecutor} within runnable threads
     *
     * @param connection
     * @param payload
     */
    protected abstract void executePayload(Connection connection, Payload payload);

    /**
     * When a payload is retrieved by {@link DataReceiveHelper} it will call this method immediately
     *
     * @param connection
     * @param payload
     */
    public void executeTask(Connection connection, Payload payload) {
        if(taskExecutionRequests == null) {
            setupTaskExecutionQueueAndThreads();
        }

        taskExecutionRequests.offer(new TaskExecutionRequest(this, connection, payload));
    }

    private void setupTaskExecutionQueueAndThreads() {
        taskExecutionRequests = new ArrayBlockingQueue<>(queueCapacity);
        taskExecutors = new ArrayList<>();
        taskExecutorThreads = new ArrayList<>();

        for(int i = 0; i < numberThreads; i++) {
            TaskExecutor taskExecutor = new TaskExecutor(taskExecutionRequests);
            taskExecutors.add(taskExecutor);
            Thread taskExecutorThread = new Thread(taskExecutor);
            taskExecutorThreads.add(taskExecutorThread);
            taskExecutorThread.start();
        }
    }

    public void shutDownTaskExecutors() {
        if(taskExecutorThreads != null) {
            for (Thread thread : taskExecutorThreads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {}
            }
        }

        if(taskExecutors != null) {
            for (TaskExecutor taskExecutor : taskExecutors) {
                taskExecutor.stop();
            }
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setEnableCloudProcessing(boolean enableCloudProcessing) {
        this.enableCloudProcessing = enableCloudProcessing;
    }

    public boolean hasEnableCloudProcessing() {
        return enableCloudProcessing;
    }

    public boolean isRequiresReturnData() {
        return requiresReturnData;
    }

    public void setRequiresReturnData(boolean requiresReturnData) {
        this.requiresReturnData = requiresReturnData;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public int getNumberThreads() {
        return numberThreads;
    }

    public void setNumberThreads(int numberThreads) {
        this.numberThreads = numberThreads;
    }

}
