package com.dakafak.simpleclients.examples.terminal;

import com.dakafak.simpleclients.connection.Connection;
import com.dakafak.simpleclients.server.data.payload.Payload;
import com.dakafak.simpleclients.server.data.task.Task;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.dakafak.simpleclients.examples.MyPayloadTypes.ALL_ACTION_RECORDS;

public class ActionTask extends Task {

	ConcurrentLinkedQueue<ActionRecord> actionRecords;

	public ActionTask(ConcurrentLinkedQueue<ActionRecord> actionRecords) {
		this.actionRecords = actionRecords;
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
			connection.sendData(allRecordsPayload);
		}
	}
}
