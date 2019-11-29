package dev.fanger.simpleclients;

import dev.fanger.simpleclients.server.data.task.Task;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public abstract class TaskedService {

    private ConcurrentHashMap<String, Task> tasks = new ConcurrentHashMap<>();

    TaskedService(List<Task> tasks) {
        for(Task task : tasks) {
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
        tasks.put(task.getUrl(), task);
    }

    /**
     * @return all tasks for this tasked service
     */
    public ConcurrentHashMap<String, Task> getTasks() {
        return tasks;
    }

}
