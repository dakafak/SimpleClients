package dev.fanger.simpleclients.examples.loadtest;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.examples.MyPayloadTypes;
import dev.fanger.simpleclients.examples.connection.ConnectionRequest;
import dev.fanger.simpleclients.examples.loadtest.results.TestResult;
import dev.fanger.simpleclients.examples.loadtest.results.TestType;
import dev.fanger.simpleclients.examples.terminal.Action;
import dev.fanger.simpleclients.server.data.payload.Payload;

import static dev.fanger.simpleclients.examples.MyPayloadTypes.ACTION;
import static dev.fanger.simpleclients.examples.MyPayloadTypes.CONNECTION_REQUEST;

public class ClientExample {

	private Connection connection;
	private int sessionId;
	private TestResult testResult;

	public ClientExample(Connection connection,
						 int sessionId,
						 int pingTestsToRun,
						 int actionTestsToRun,
						 int currentConnections) {
		this.connection = connection;
		this.sessionId = sessionId;

		testResult = new TestResult(currentConnections);

		connectToServer();
		runPingTests(pingTestsToRun);
		sendActions(actionTestsToRun);
	}

	private void connectToServer() {
		ConnectionRequest connectionRequest = new ConnectionRequest("Test client " + connection.getId(), sessionId);
		connection.sendData(new Payload(connectionRequest, CONNECTION_REQUEST));
	}

	long totalPingTestRunTime;
	double averagePingTime;

	private void runPingTests(int pingTestsToRun) {
		totalPingTestRunTime = 0;
		averagePingTime = 0;

		for(int i = 0; i < pingTestsToRun; i++) {
			long startOfPingTest = System.nanoTime();

			Payload<String> testPayload = new Payload<>("Test Ping Payload ", MyPayloadTypes.PING);
			connection.sendData(testPayload);
			connection.retrieveData();

			long endOfPingTest = System.nanoTime();
			totalPingTestRunTime += endOfPingTest - startOfPingTest;
		}

		averagePingTime = totalPingTestRunTime / (double) pingTestsToRun;
		testResult.getAverageTimePerTestTime().put(TestType.PING, averagePingTime);
		System.out.println("Finished ping test for: " + connection.getId());

		//TODO update this to store run time and number of clients to a Results.java object in a map for client id
		//TODO also update each type of Test within load test. To simplify the run time and average run time data
	}

	long totalActionTestRunTime;
	double averageActionTime;

	private void sendActions(int actionTestsToRun) {
		totalActionTestRunTime = 0;
		averageActionTime = 0;

		for(int i = 0; i < actionTestsToRun; i++) {
			long startOfActionTest = System.nanoTime();

			Payload<Action> newPayload = new Payload<>(Action.values()[(int)Math.floor(Math.random() * Action.values().length)], ACTION);
			connection.sendData(newPayload);
			connection.retrieveData();

			long endOfActionTest = System.nanoTime();
			totalActionTestRunTime += endOfActionTest - startOfActionTest;
		}

		averageActionTime = totalActionTestRunTime / (double) actionTestsToRun;
		testResult.getAverageTimePerTestTime().put(TestType.ACTION, averageActionTime);
		System.out.println("Finished action test for: " + connection.getId());
	}

	public double getAveragePingTimeInNanoSeconds() {
		return averagePingTime;
	}

	public double getAverageActionTestTimeInNanoSeconds() {
		return averageActionTime;
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

	public TestResult getTestResult() {
		return testResult;
	}

	public void setTestResult(TestResult testResult) {
		this.testResult = testResult;
	}
}
