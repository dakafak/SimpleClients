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
	double averagePingTime;

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

		averagePingTime = numberPingTestRuns / (double) totalPingTestRunTime;
		System.out.println("Finished ping test for: " + connection.getId());
	}

	long numberActionTestsRun;
	long totalActionTestRunTime;
	double averageActionTime;

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

		averageActionTime = numberActionTestsRun / (double) totalActionTestRunTime;;
		System.out.println("Finished action test for: " + connection.getId());
	}

	public double getAveragePingTimeInNanoSeconds() {
		return averagePingTime;
	}

	public double getAverageActionTestTimeInNanoSeconds() {
		return averageActionTime;
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

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public int getTimeToRunPingTest() {
		return timeToRunPingTest;
	}

	public void setTimeToRunPingTest(int timeToRunPingTest) {
		this.timeToRunPingTest = timeToRunPingTest;
	}

	public int getTimeToRunActionTest() {
		return timeToRunActionTest;
	}

	public void setTimeToRunActionTest(int timeToRunActionTest) {
		this.timeToRunActionTest = timeToRunActionTest;
	}
}
