package dev.fanger.simpleclients.examples.Tasks;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.examples.Tasks.data.connection.User;
import dev.fanger.simpleclients.examples.Tasks.data.terminal.Action;
import dev.fanger.simpleclients.examples.Tasks.data.terminal.ActionRecord;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ActionTask extends Task {

	private ConcurrentLinkedQueue<ActionRecord> actionRecords;
	private ConcurrentHashMap<Integer, List<User>> sessionIdToUsers;
	private ConcurrentHashMap<UUID, Integer> connectionIdToSessionId;

	public ActionTask(String url,
					  ConcurrentLinkedQueue<ActionRecord> actionRecords,
					  ConcurrentHashMap<Integer, List<User>> sessionIdToUsers,
					  ConcurrentHashMap<UUID, Integer> connectionIdToSessionId) {
		super(url);
		this.actionRecords = actionRecords;
		this.sessionIdToUsers = sessionIdToUsers;
		this.connectionIdToSessionId = connectionIdToSessionId;
	}

	@Override
	public void executeTask(Connection connection, Payload payload) {
		if(payload.getData() instanceof Action) {
			ActionRecord actionRecord = new ActionRecord();
			actionRecord.setActionPerformed((Action) payload.getData());
			actionRecord.setTimestamp(System.nanoTime());
			actionRecord.setConnectionWhoMadeAction(connection.getId());

			actionRecords.add(actionRecord);

			LinkedList<ActionRecord> allCurrentActions = new LinkedList<>();
			allCurrentActions.addAll(actionRecords);
			Payload<LinkedList<ActionRecord>> allRecordsPayload = new Payload<>(allCurrentActions, "/test/action/all");

			List<User> usersToSendDataTo = sessionIdToUsers.get(connectionIdToSessionId.get(connection.getId()));
			for(int i = 0; i < usersToSendDataTo.size(); i++) {
				User user = usersToSendDataTo.get(i);
				user.getConnection().sendData(allRecordsPayload);
			}
		}
	}
}
