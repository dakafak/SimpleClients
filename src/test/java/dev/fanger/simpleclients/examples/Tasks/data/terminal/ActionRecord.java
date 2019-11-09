package dev.fanger.simpleclients.examples.Tasks.data.terminal;

import java.io.Serializable;
import java.util.UUID;

public class ActionRecord implements Serializable {

	private UUID connectionWhoMadeAction;
	private Action actionPerformed;
	private long timestamp;

	public UUID getConnectionWhoMadeAction() {
		return connectionWhoMadeAction;
	}

	public void setConnectionWhoMadeAction(UUID connectionWhoMadeAction) {
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
