package dev.fanger.simpleclients;

import dev.fanger.simpleclients.server.data.task.Task;

import java.util.concurrent.ConcurrentHashMap;

public abstract class SimpleClientManager {

    private ConcurrentHashMap<String, Task> tasks = new ConcurrentHashMap<>();

    public void addTask(Task task) {
        tasks.put(task.getUrl(), task);
    }

    public ConcurrentHashMap<String, Task> getTasks() {
        return tasks;
    }

}
