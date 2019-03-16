package com.dakafak.simpleclients.examples;

import com.dakafak.simpleclients.examples.ping.PingTask;
import com.dakafak.simpleclients.examples.terminal.ActionRecord;
import com.dakafak.simpleclients.examples.terminal.ActionTask;
import com.dakafak.simpleclients.server.ConnectionHandler;

import java.util.concurrent.ConcurrentLinkedQueue;

import static com.dakafak.simpleclients.examples.MyPayloadTypes.ACTION;

public class ServerExample {

	public static void main(String[] args) {
		ConcurrentLinkedQueue<ActionRecord> actionRecords = new ConcurrentLinkedQueue<>();
		ConnectionHandler connectionHandler = new ConnectionHandler(1776);
		connectionHandler.addTask(ACTION, new ActionTask(actionRecords));
		connectionHandler.addTask(MyPayloadTypes.PING, new PingTask());
		connectionHandler.startListeningForConnections();
	}

}
