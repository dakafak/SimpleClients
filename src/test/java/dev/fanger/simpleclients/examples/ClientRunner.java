package dev.fanger.simpleclients.examples;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.examples.loadtest.results.ClientTestResults;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientRunner implements Runnable {

	private Connection connection;
	private int sessionId;
	private ConcurrentLinkedQueue<ClientRunner> clientRunners;
	private ConcurrentLinkedQueue<ClientRunner> finishedClientRunners;
	private ClientExample clientExample;
	private int pingTestsToRun;
	private int actionTestsToRun;
	private ClientTestResults clientTestResults;
	private int clientsRunningDuringTest;

	public ClientRunner(Connection connection,
						int sessionId,
						ConcurrentLinkedQueue<ClientRunner> clientRunners,
						ConcurrentLinkedQueue<ClientRunner> finishedClientRunners,
						int pingTestsToRun,
						int actionTestsToRun,
						ClientTestResults clientTestResults,
						int clientsRunningDuringTest) {
		this.connection = connection;
		this.sessionId = sessionId;
		this.clientRunners = clientRunners;
		this.finishedClientRunners = finishedClientRunners;
		this.pingTestsToRun = pingTestsToRun;
		this.actionTestsToRun = actionTestsToRun;
		this.clientTestResults = clientTestResults;
		this.clientsRunningDuringTest = clientsRunningDuringTest;
	}

	@Override
	public void run() {
		clientExample = new ClientExample(connection, sessionId, pingTestsToRun, actionTestsToRun, clientsRunningDuringTest);
		connection.shutDownClient();
		clientTestResults.addTestResult(clientExample.getTestResult());
		finishedClientRunners.add(this);
		clientRunners.remove(this);

		connection.shutDownClient();
	}

	public ClientExample getClientExample() {
		return clientExample;
	}

}
