package dev.fanger.simpleclients.server.data.task;

import dev.fanger.simpleclients.logging.Level;
import dev.fanger.simpleclients.logging.Logger;

import java.util.concurrent.ArrayBlockingQueue;

public class TaskExecutor implements Runnable {

    private ArrayBlockingQueue<TaskExecutionRequest> taskExecutionRequests;
    private boolean keepRunning = true;

    public TaskExecutor(ArrayBlockingQueue<TaskExecutionRequest> taskExecutionRequests) {
        this.taskExecutionRequests = taskExecutionRequests;
    }

    @Override
    public void run() {
        int lastQueueSize = 0;

        while(keepRunning) {
            TaskExecutionRequest currentExecutionRequest = taskExecutionRequests.poll();

            if(currentExecutionRequest != null) {
                // Debug thing for checking queue size motion
                int currentQueueSize = taskExecutionRequests.size();
                if(currentQueueSize != lastQueueSize) {
                    lastQueueSize = currentQueueSize;
                    Logger.log(Level.DEBUG,
                            "Polled TaskExecutionRequest, remaining queue size["
                                    + currentExecutionRequest.getTask().getClass()
                                    + "]: " + taskExecutionRequests.size());
                }

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
