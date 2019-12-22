package dev.fanger.simpleclients.server.cloud;

import dev.fanger.simpleclients.server.data.task.Task;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class TaskLoadManager {

    /**
     * Incremented when a task starts execution and decremented when a task finishes execution
     */
    private ConcurrentHashMap<Class, TaskLoad> taskToLoad;

    public TaskLoadManager(Collection<Task> allTasks) {
        taskToLoad = new ConcurrentHashMap<>();

        for(Task task : allTasks) {
            taskToLoad.put(task.getClass(), new TaskLoad());
        }
    }

    public synchronized void incrementServerLoad(Class taskClass) {
        taskToLoad.get(taskClass).incrementTaskLoad();
    }

    public synchronized void decrementServerLoad(Class taskClass) {
        taskToLoad.get(taskClass).decrementTaskLoad();
    }

    public synchronized int getCurrentServerLoad(Class taskClass) {
        return taskToLoad.get(taskClass).getCurrentTaskLoad();
    }

    public Collection<Class> getTaskClassList() {
        return taskToLoad.keySet();
    }

}
