package dev.fanger.simpleclients.examples.client;

import dev.fanger.simpleclients.examples.TraditionClientExample;
import dev.fanger.simpleclients.examples.loadtest.results.ClientTestResults;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientRunner implements Runnable {

	private int sessionId;
	private TraditionClientExample traditionClientExample;
	private ClientTestResults clientTestResults;
	private int clientsRunningDuringTest;

	public ClientRunner(int sessionId,
						ClientTestResults clientTestResults,
						int clientsRunningDuringTest) {
		this.sessionId = sessionId;
		this.clientTestResults = clientTestResults;
		this.clientsRunningDuringTest = clientsRunningDuringTest;
	}

	@Override
	public void run() {
		traditionClientExample = new TraditionClientExample(sessionId, clientsRunningDuringTest);
		traditionClientExample.runTests();
		clientTestResults.addTestResult(traditionClientExample.getTestResult());
		traditionClientExample.shutDownClientExample();
	}

}
