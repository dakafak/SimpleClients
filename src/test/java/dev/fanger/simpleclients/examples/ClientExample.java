package dev.fanger.simpleclients.examples;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.examples.Tasks.data.connection.ConnectionRequest;
import dev.fanger.simpleclients.examples.loadtest.results.TestResult;
import dev.fanger.simpleclients.examples.loadtest.results.TestType;
import dev.fanger.simpleclients.examples.Tasks.data.terminal.Action;
import dev.fanger.simpleclients.server.data.payload.Payload;

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
		connection.sendData(new Payload(connectionRequest, "/client/connect"));
	}

	long totalPingTestRunTime;
	double averagePingTime;

	private void runPingTests(int pingTestsToRun) {
		totalPingTestRunTime = 0;
		averagePingTime = 0;

		for(int i = 0; i < pingTestsToRun; i++) {
			long startOfPingTest = System.nanoTime();

			Payload<String> testPayload = new Payload<>("Test Ping Payload ", "/test/ping");
			connection.sendData(testPayload);
			connection.retrieveData();

			long endOfPingTest = System.nanoTime();
			totalPingTestRunTime += endOfPingTest - startOfPingTest;
		}

		averagePingTime = totalPingTestRunTime / (double) pingTestsToRun;
		testResult.getAverageTimePerTestTime().put(TestType.PING, averagePingTime);
	}

	long totalActionTestRunTime;
	double averageActionTime;

	private void sendActions(int actionTestsToRun) {
		totalActionTestRunTime = 0;
		averageActionTime = 0;

		for(int i = 0; i < actionTestsToRun; i++) {
			long startOfActionTest = System.nanoTime();

			Payload<Action> newPayload = new Payload<>(Action.values()[(int)Math.floor(Math.random() * Action.values().length)], "/test/action");
			connection.sendData(newPayload);
			connection.retrieveData();

			long endOfActionTest = System.nanoTime();
			totalActionTestRunTime += (endOfActionTest - startOfActionTest);
		}

		averageActionTime = totalActionTestRunTime / (double) actionTestsToRun;
		testResult.getAverageTimePerTestTime().put(TestType.ACTION, averageActionTime);
	}


	public TestResult getTestResult() {
		return testResult;
	}

}
