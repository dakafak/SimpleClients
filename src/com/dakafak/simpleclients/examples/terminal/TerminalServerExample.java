package com.dakafak.simpleclients.examples.terminal;

import com.dakafak.simpleclients.server.ConnectionHandler;

import java.util.concurrent.ConcurrentLinkedQueue;

import static com.dakafak.simpleclients.examples.MyPayloadTypes.ACTION;

public class TerminalServerExample {

	public static void main(String[] args) {
		ConcurrentLinkedQueue<ActionRecord> actionRecords = new ConcurrentLinkedQueue<>();
		ConnectionHandler connectionHandler = new ConnectionHandler(1776);
		ActionTask actionTask = new ActionTask(actionRecords);
		connectionHandler.addTask(ACTION, actionTask);
		connectionHandler.startListeningForConnections();
	}

}
