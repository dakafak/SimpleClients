package dev.fanger.simpleclients.examples.client;

import dev.fanger.simpleclients.examples.ClientExample;
import dev.fanger.simpleclients.examples.loadtest.results.ClientTestResults;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientRunner implements Runnable {

	private int sessionId;
	private ConcurrentLinkedQueue<ClientRunner> clientRunners;
	private ConcurrentLinkedQueue<ClientRunner> finishedClientRunners;
	private ClientExample clientExample;
	private ClientTestResults clientTestResults;
	private int clientsRunningDuringTest;

	public ClientRunner(int sessionId,
						ConcurrentLinkedQueue<ClientRunner> clientRunners,
						ConcurrentLinkedQueue<ClientRunner> finishedClientRunners,
						ClientTestResults clientTestResults,
						int clientsRunningDuringTest) {
		this.sessionId = sessionId;
		this.clientRunners = clientRunners;
		this.finishedClientRunners = finishedClientRunners;
		this.clientTestResults = clientTestResults;
		this.clientsRunningDuringTest = clientsRunningDuringTest;
	}

	@Override
	public void run() {
		clientExample = new ClientExample(sessionId, clientsRunningDuringTest);
		clientExample.runTests();
		clientTestResults.addTestResult(clientExample.getTestResult());
		clientExample.shutDownClientExample();

		finishedClientRunners.add(this);
		clientRunners.remove(this);
	}

}
