package com.dakafak.simpleclients.examples;

import com.dakafak.simpleclients.connection.Connection;
import com.dakafak.simpleclients.server.data.payload.Payload;

public class ClientExample {

	public ClientExample() {
		System.out.println("-- Setting up client socket");
		Connection connection = Connection.newConnection("127.0.0.1", 1776);

		int payloadsToSend = 25;

		for(int i = 0; i < payloadsToSend; i++) {
			Payload<String> testPayload = new Payload<>("Test Payload " + i, MyPayloadTypes.PING);
			System.out.print("Sending payload: " + testPayload + " | ");
			long timeBeforeSend = System.nanoTime();
			connection.sendData(testPayload);
			Payload receivedPayload = connection.retrieveData();
			long timeAfterSend = System.nanoTime();
			System.out.print(((timeAfterSend - timeBeforeSend) / 1000000.0) + "ms -> Retrieved payload: " + receivedPayload + System.lineSeparator());
		}
	}

}
