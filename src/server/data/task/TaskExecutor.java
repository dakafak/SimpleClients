package server.data.task;

import connection.Connection;
import server.data.payload.Payload;

public class TaskExecutor implements Runnable {

	private Task task;
	private Connection connection;
	private Payload payload;

	public TaskExecutor(Task task, Connection connection, Payload payload) {
		this.task = task;
		this.connection = connection;
		this.payload = payload;
	}

	@Override
	public void run() {
		task.executeTask(connection, payload);
	}

}
