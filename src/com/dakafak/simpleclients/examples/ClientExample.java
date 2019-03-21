package com.dakafak.simpleclients.examples;

import com.dakafak.simpleclients.connection.Connection;
import com.dakafak.simpleclients.examples.connection.ConnectionRequest;
import com.dakafak.simpleclients.examples.terminal.Action;
import com.dakafak.simpleclients.examples.terminal.ActionRecord;
import com.dakafak.simpleclients.server.data.payload.Payload;

import java.util.LinkedList;

import static com.dakafak.simpleclients.examples.MyPayloadTypes.ACTION;
import static com.dakafak.simpleclients.examples.MyPayloadTypes.CONNECTION_REQUEST;

public class ClientExample {

	private Connection connection;
	int pingPayloadsToSend = 3;
	int actionPayloadsToSend = 3;

	public ClientExample() {
		System.out.println("-- Setting up client socket");
		connection = Connection.newConnection("127.0.0.1", 1776);

		System.out.println("-- Testing connection to server -- ");
		connectToServer();

		System.out.println("-- Testing ping -- ");
		sendPing();

		System.out.println("-- Testing actions -- ");
		sendActions();
	}

	private void connectToServer() {
		ConnectionRequest connectionRequest = new ConnectionRequest("Test client " + (int)Math.round(Math.random()*100), 1);
		connection.sendData(new Payload(connectionRequest, CONNECTION_REQUEST));
	}

	private void sendPing() {
		for(int i = 0; i < pingPayloadsToSend; i++) {
			Payload<String> testPayload = new Payload<>("Test Payload " + i, MyPayloadTypes.PING);
			System.out.print("Sending payload: " + testPayload + " | ");
			long timeBeforeSend = System.nanoTime();
			connection.sendData(testPayload);
			Payload receivedPayload = connection.retrieveData();
			long timeAfterSend = System.nanoTime();
			System.out.print(((timeAfterSend - timeBeforeSend) / 1000000.0) + "ms -> Retrieved payload: " + receivedPayload + System.lineSeparator());
		}
	}

	private void sendActions() {
		for(int i = 0; i < actionPayloadsToSend; i++) {
			Payload<Action> newPayload = new Payload<>(Action.values()[(int)Math.floor(Math.random() * Action.values().length)], ACTION);
			System.out.println("Sending payload: " + newPayload);
			connection.sendData(newPayload);

			Payload readResponseFromServer = connection.retrieveData();
			System.out.println("Action records: " + (LinkedList<ActionRecord>)readResponseFromServer.getData());
		}
	}

}
