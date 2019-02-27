package server.handlerthreads;

import connection.Connection;
import server.data.payload.Payload;
import server.data.task.Task;
import server.data.task.TaskExecutor;

import java.util.concurrent.*;

public class TaskExecutorService implements Runnable {

	private int numberOfThreads;
	private boolean continueRunning;
	private ConcurrentHashMap<Enum, Task> tasks;
	private ConcurrentLinkedQueue<TaskExecutor> tasksToExecute;
	private long timeoutInMilliseconds;

	public TaskExecutorService(int numberOfThreads, long timeoutInMilliseconds) {
		this.numberOfThreads = numberOfThreads;
		this.timeoutInMilliseconds = timeoutInMilliseconds;

		tasks = new ConcurrentHashMap<>();
		tasksToExecute = new ConcurrentLinkedQueue<>();
	}

	public void setContinueRunning(boolean continueRunning) {
		this.continueRunning = continueRunning;
	}

	public void addTask(Enum taskType, Task task){
		tasks.put(taskType, task);
	}

	public void addTaskToExecute(Connection connection, Payload payload) {
		if(tasks.containsKey(payload.getPayloadType())) {
			TaskExecutor taskExecutor = new TaskExecutor(tasks.get(payload.getPayloadType()), connection, payload);
			tasksToExecute.add(taskExecutor);
		}
	}

	public boolean containsTaskType(Enum taskType) {
		return tasks.containsKey(taskType);
	}

	@Override
	public void run() {
		while(continueRunning) {
			if(!tasksToExecute.isEmpty()) {
				ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
				for(int i = 0; i < tasksToExecute.size(); i++) {
					TaskExecutor taskExecutor = tasksToExecute.poll();
					executorService.execute(taskExecutor);
				}

				try {
					executorService.awaitTermination(timeoutInMilliseconds, TimeUnit.MILLISECONDS);
					executorService.shutdown();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
