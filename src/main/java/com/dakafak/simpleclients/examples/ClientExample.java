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
	int sessionId;

	int timeToRunPingTest = 1000;
	int timeToRunActionTest = 1000;

	public ClientExample(Connection connection, int sessionId) {
		this.connection = connection;
		this.sessionId = sessionId;

		connectToServer();
		sendPing();
		sendActions();
	}

	private void connectToServer() {
		ConnectionRequest connectionRequest = new ConnectionRequest("Test client " + connection.getId(), sessionId);
		connection.sendData(new Payload(connectionRequest, CONNECTION_REQUEST));
	}

	long numberPingTestRuns;
	long totalPingTestRunTime;

	private void sendPing() {
		long pingTestFinishTime = System.currentTimeMillis() + timeToRunPingTest;
		while(System.currentTimeMillis() < pingTestFinishTime) {
			long startOfPingTest = System.nanoTime();
			Payload<String> testPayload = new Payload<>("Test Ping Payload ", MyPayloadTypes.PING);
			connection.sendData(testPayload);
			connection.retrieveData();
			long endOfPingTest = System.nanoTime();
			totalPingTestRunTime += endOfPingTest - startOfPingTest;
			numberPingTestRuns++;
		}
		System.out.println("Finished ping test for: " + connection.getId());
	}

	long numberActionTestsRun;
	long totalActionTestRunTime;

	private void sendActions() {
		long actionTestFinishTime = System.currentTimeMillis() + timeToRunActionTest;
		while(System.currentTimeMillis() < actionTestFinishTime) {
			long startOfActionTest = System.nanoTime();
			Payload<Action> newPayload = new Payload<>(Action.values()[(int)Math.floor(Math.random() * Action.values().length)], ACTION);
			connection.sendData(newPayload);
			connection.retrieveData();
			long endOfActionTest = System.nanoTime();
			totalActionTestRunTime += endOfActionTest - startOfActionTest;
			numberActionTestsRun++;
		}
		System.out.println("Finished action test for: " + connection.getId());
	}

	public double getAveragePingTimeInNanoSeconds() {
		return numberPingTestRuns / (double) totalPingTestRunTime;
	}

	public double getAverageActionTestTimeInNanoSeconds() {
		return numberActionTestsRun / (double) totalActionTestRunTime;
	}

	public long getNumberPingTestRuns() {
		return numberPingTestRuns;
	}

	public void setNumberPingTestRuns(long numberPingTestRuns) {
		this.numberPingTestRuns = numberPingTestRuns;
	}

	public long getTotalPingTestRunTime() {
		return totalPingTestRunTime;
	}

	public void setTotalPingTestRunTime(long totalPingTestRunTime) {
		this.totalPingTestRunTime = totalPingTestRunTime;
	}

	public long getNumberActionTestsRun() {
		return numberActionTestsRun;
	}

	public void setNumberActionTestsRun(long numberActionTestsRun) {
		this.numberActionTestsRun = numberActionTestsRun;
	}

	public long getTotalActionTestRunTime() {
		return totalActionTestRunTime;
	}

	public void setTotalActionTestRunTime(long totalActionTestRunTime) {
		this.totalActionTestRunTime = totalActionTestRunTime;
	}
}
