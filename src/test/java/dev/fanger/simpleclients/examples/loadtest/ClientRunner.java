package dev.fanger.simpleclients.examples.loadtest;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.examples.loadtest.results.ClientTestResults;
import dev.fanger.simpleclients.examples.loadtest.results.TestResult;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientRunner implements Runnable {

	private Connection connection;
	private int sessionId;
	private ConcurrentLinkedQueue<ClientRunner> clientRunners;
	private ConcurrentLinkedQueue<ClientRunner> finishedClientRunners;
	private ClientExample clientExample;
	int pingTestsToRun;
	int actionTestsToRun;
	private ClientTestResults clientTestResults;

	public ClientRunner(Connection connection,
						int sessionId,
						ConcurrentLinkedQueue<ClientRunner> clientRunners,
						ConcurrentLinkedQueue<ClientRunner> finishedClientRunners,
						int pingTestsToRun,
						int actionTestsToRun,
						ClientTestResults clientTestResults) {
		this.connection = connection;
		this.sessionId = sessionId;
		this.clientRunners = clientRunners;
		this.finishedClientRunners = finishedClientRunners;
		this.pingTestsToRun = pingTestsToRun;
		this.actionTestsToRun = actionTestsToRun;
		this.clientTestResults = clientTestResults;
	}

	@Override
	public void run() {
		clientExample = new ClientExample(connection, sessionId, pingTestsToRun, actionTestsToRun, clientRunners.size());
		connection.shutDownClient();
		clientTestResults.addTestResult(clientExample.getTestResult());
		finishedClientRunners.add(this);
		clientRunners.remove(this);
	}

	public ClientExample getClientExample() {
		return clientExample;
	}

}
