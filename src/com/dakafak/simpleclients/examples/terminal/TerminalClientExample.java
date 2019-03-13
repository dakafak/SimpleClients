package com.dakafak.simpleclients.examples.terminal;

import com.dakafak.simpleclients.connection.Connection;
import com.dakafak.simpleclients.server.data.payload.Payload;

import java.util.LinkedList;

import static com.dakafak.simpleclients.examples.MyPayloadTypes.ACTION;

public class TerminalClientExample {

	public TerminalClientExample() {
		Connection connection = Connection.newConnection("127.0.0.1", 1776);

		int payloadsToSend = 5;

		for(int i = 0; i < payloadsToSend; i++) {
			Payload<Action> newPayload = new Payload<>(Action.values()[(int)Math.floor(Math.random() * Action.values().length - 1)], ACTION);
			System.out.println("Sending payload: " + newPayload);
			connection.sendData(newPayload);

			Payload readResponseFromServer = connection.retrieveData();
			System.out.println("Action records: " + (LinkedList<ActionRecord>)readResponseFromServer.getData());
		}
	}

}
