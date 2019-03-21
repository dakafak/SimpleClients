package com.dakafak.simpleclients.examples.terminal;

import com.dakafak.simpleclients.connection.Connection;
import com.dakafak.simpleclients.connection.Id;
import com.dakafak.simpleclients.examples.connection.User;
import com.dakafak.simpleclients.server.data.payload.Payload;
import com.dakafak.simpleclients.server.data.task.Task;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.dakafak.simpleclients.examples.MyPayloadTypes.ALL_ACTION_RECORDS;

public class ActionTask extends Task {

	private ConcurrentLinkedQueue<ActionRecord> actionRecords;
	private ConcurrentHashMap<Integer, List<User>> sessionIdToUsers;
	private ConcurrentHashMap<Id, Integer> connectionIdToSessionId;

	public ActionTask(ConcurrentLinkedQueue<ActionRecord> actionRecords,
					  ConcurrentHashMap<Integer, List<User>> sessionIdToUsers,
					  ConcurrentHashMap<Id, Integer> connectionIdToSessionId) {
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
			System.out.println(actionRecord);

			LinkedList<ActionRecord> allCurrentActions = new LinkedList<>();
			allCurrentActions.addAll(actionRecords);
			Payload<LinkedList<ActionRecord>> allRecordsPayload = new Payload<>(allCurrentActions, ALL_ACTION_RECORDS);

			List<User> usersToSendDataTo = sessionIdToUsers.get(connectionIdToSessionId.get(connection.getId()));
			for(int i = 0; i < usersToSendDataTo.size(); i++) {
				User user = usersToSendDataTo.get(i);
				user.getConnection().sendData(allRecordsPayload);
			}
		}
	}
}
