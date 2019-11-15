package dev.fanger.simpleclients;

import dev.fanger.simpleclients.server.data.task.Task;

import java.util.concurrent.ConcurrentHashMap;

public abstract class TaskedService {

    private ConcurrentHashMap<String, Task> tasks = new ConcurrentHashMap<>();

    /**
     * Shared method for tasked services.
     * Used by {@link SimpleClient} and {@link SimpleServer}
     *
     * @param task
     */
    public void addTask(Task task) {
        tasks.put(task.getUrl(), task);
    }

    /**
     * @return all tasks for this tasked service
     */
    public ConcurrentHashMap<String, Task> getTasks() {
        return tasks;
    }

}
