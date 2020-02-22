package dev.fanger.simpleclients;

import dev.fanger.simpleclients.annotations.AdvancedTaskProperties;
import dev.fanger.simpleclients.exceptions.DuplicateTaskException;
import dev.fanger.simpleclients.server.data.task.Task;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public abstract class TaskedService {

    private ConcurrentHashMap<String, Task> tasks = new ConcurrentHashMap<>();

    /**
     * Throws {@link DuplicateTaskException} if the desired task url is already being used by an existing task
     * Primarily used to block overriding of built in tasks
     *
     * @param tasks
     * @throws DuplicateTaskException
     */
    TaskedService(List<Task> tasks) throws DuplicateTaskException {
        for(Task task : tasks) {
            if(tasks.contains(task.getUrl())) {
                throw new DuplicateTaskException();
            }

            addTask(task);
        }
    }

    /**
     * Shared method for tasked services.
     * Used by {@link SimpleClient} and {@link SimpleServer}
     *
     * @param task
     */
    void addTask(Task task) {
        if(task.getClass().isAnnotationPresent(AdvancedTaskProperties.class)) {
            AdvancedTaskProperties advancedTaskProperties = task.getClass().getAnnotation(AdvancedTaskProperties.class);
            task.setNumberThreads(advancedTaskProperties.numberThreads());
            task.setQueueCapacity(advancedTaskProperties.queueCapacity());
            task.setEnableCloudProcessing(advancedTaskProperties.enableCloudProcessing());
            task.setRequiresReturnData(advancedTaskProperties.requiresReturnData());
        }

        tasks.put(task.getUrl(), task);
    }

    /**
     * @return all tasks for this tasked service
     */
    public ConcurrentHashMap<String, Task> getTasks() {
        return tasks;
    }

}
