package com.dakafak.simpleclients.examples.terminal;

import com.dakafak.simpleclients.connection.Id;

import java.io.Serializable;

public class ActionRecord implements Serializable {

	private Id connectionWhoMadeAction;
	private Action actionPerformed;
	private long timestamp;

	public Id getConnectionWhoMadeAction() {
		return connectionWhoMadeAction;
	}

	public void setConnectionWhoMadeAction(Id connectionWhoMadeAction) {
		this.connectionWhoMadeAction = connectionWhoMadeAction;
	}

	public Action getActionPerformed() {
		return actionPerformed;
	}

	public void setActionPerformed(Action actionPerformed) {
		this.actionPerformed = actionPerformed;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "ActionRecord{" +
				"connectionWhoMadeAction=" + connectionWhoMadeAction +
				", actionPerformed=" + actionPerformed +
				", timestamp=" + timestamp +
				'}';
	}
}
