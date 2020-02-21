package dev.fanger.simpleclients.server.data.task;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.logging.Level;
import dev.fanger.simpleclients.logging.Logger;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.handlerthreads.datahelper.DataReceiveHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public abstract class Task {

    private String url;
    private boolean allowCloudProcessing;
    private boolean requiresReturnData;
    private int maxLoadForCloud;
    private int numberThreadsForCloudTaskProcessors;

    private int queueCapacity = 128;
    private int numberThreads = 1;
    private BlockingQueue<TaskExecutionRequest> taskExecutionRequests;
    private List<TaskExecutor> taskExecutors;
    private List<Thread> taskExecutorThreads;

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

        taskExecutionRequests.add(new TaskExecutionRequest(this, connection, payload));
    }

    protected abstract void executePayload(Connection connection, Payload payload);

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
        for(Thread thread : taskExecutorThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) { }
        }

        for(TaskExecutor taskExecutor : taskExecutors) {
            taskExecutor.stop();
        }
    }

    private static class TaskExecutor implements Runnable {

        private BlockingQueue<TaskExecutionRequest> taskExecutionRequests;
        private boolean keepRunning = true;

        public TaskExecutor(BlockingQueue<TaskExecutionRequest> taskExecutionRequests) {
            this.taskExecutionRequests = taskExecutionRequests;
        }

        @Override
        public void run() {
            int lastQueueSize = 0;

            while(keepRunning) {
                TaskExecutionRequest currentExecutionRequest = taskExecutionRequests.poll();

                // Debug thing for checking queue size motion
                int currentQueueSize = taskExecutionRequests.size();
                if(currentQueueSize != lastQueueSize) {
                    lastQueueSize = currentQueueSize;
                    Logger.log(Level.DEBUG, "Polled TaskExecutionRequest, remaining queue size: " + taskExecutionRequests.size());
                }

                if(currentExecutionRequest != null) {
                    Task task = currentExecutionRequest.getTask();
                    task.executePayload(
                            currentExecutionRequest.getConnection(),
                            currentExecutionRequest.getPayload());
                }
            }
        }

        public void stop() {
            this.keepRunning = false;
        }

    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setAllowCloudProcessing(boolean allowCloudProcessing) {
        this.allowCloudProcessing = allowCloudProcessing;
    }

    public boolean hasAllowedCloudProcessing() {
        return allowCloudProcessing;
    }

    public boolean isRequiresReturnData() {
        return requiresReturnData;
    }

    public void setRequiresReturnData(boolean requiresReturnData) {
        this.requiresReturnData = requiresReturnData;
    }

    public int getMaxLoadForCloud() {
        return maxLoadForCloud;
    }

    public void setMaxLoadForCloud(int maxLoadForCloud) {
        this.maxLoadForCloud = maxLoadForCloud;
    }

    public int getNumberThreadsForCloudTaskProcessors() {
        return numberThreadsForCloudTaskProcessors;
    }

    public void setNumberThreadsForCloudTaskProcessors(int numberThreadsForCloudTaskProcessors) {
        this.numberThreadsForCloudTaskProcessors = numberThreadsForCloudTaskProcessors;
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
