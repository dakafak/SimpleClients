package dev.fanger.simpleclients.examples.client;

import dev.fanger.simpleclients.examples.loadtest.results.ClientTestResults;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientLoadTest {

	private int maxConcurrentConnections = 8;// Tests are run *=2 up to max number of tests to run

	private ClientTestResults clientTestResults;
	private ConcurrentLinkedQueue<ConcurrentLinkedQueue<ClientRunner>> allClientRunners;
	private ConcurrentLinkedQueue<ConcurrentLinkedQueue<Thread>> allThreadsForClientRunners;

	public ClientLoadTest() {
		clientTestResults = new ClientTestResults();
		allClientRunners = new ConcurrentLinkedQueue<>();
		allThreadsForClientRunners = new ConcurrentLinkedQueue<>();

		for(int i = 1; i <= maxConcurrentConnections; i *= 2) {
			runTests(i);
		}

		clientTestResults.calculateAndPrintTestResults();
	}

	private void runTests(int numberOfConnectionsToTest) {
		ConcurrentLinkedQueue<ClientRunner> clientRunners = new ConcurrentLinkedQueue<>();
		allClientRunners.add(clientRunners);
		ConcurrentLinkedQueue<Thread> clientRunnerThreads = new ConcurrentLinkedQueue<>();
		allThreadsForClientRunners.add(clientRunnerThreads);

		addClientsAndRunTest(numberOfConnectionsToTest, clientRunners, clientRunnerThreads);

		for(Thread thread : clientRunnerThreads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Finished running load test for: " + numberOfConnectionsToTest + " connections");
	}

	private void addClientsAndRunTest(int numberOfConnectionsToTest,
									  ConcurrentLinkedQueue<ClientRunner> clientRunners,
									  ConcurrentLinkedQueue<Thread> clientRunnerThreads) {
		for(int i = 0; i < numberOfConnectionsToTest; i++) {
			ClientRunner clientRunner = new ClientRunner(i,
					clientTestResults,
					numberOfConnectionsToTest);
			clientRunners.add(clientRunner);
		}

		for(ClientRunner clientRunner : clientRunners) {
			Thread newClientRunnerThread = new Thread(clientRunner);
			clientRunnerThreads.add(newClientRunnerThread);
		}

		for(Thread thread : clientRunnerThreads) {
			thread.start();
		}
	}

}
