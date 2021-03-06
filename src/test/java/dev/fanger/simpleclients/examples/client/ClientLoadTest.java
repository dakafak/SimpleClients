package dev.fanger.simpleclients.examples.client;

import dev.fanger.simpleclients.examples.loadtest.results.ClientTestResults;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientLoadTest {

	private int maxConcurrentConnections = 8;// Tests are run *=2 up to max number of tests to run

	private ClientTestResults clientTestResults;

	public ClientLoadTest() {
		clientTestResults = new ClientTestResults();

		for(int i = 1; i <= maxConcurrentConnections; i *= 2) {
			runTests(i);
		}

		clientTestResults.calculateAndPrintTestResults();
	}

	private void runTests(int numberOfConnectionsToTest) {
		ConcurrentLinkedQueue<ClientRunner> clientRunners;
		ConcurrentLinkedQueue<ClientRunner> completedClientRunners;
		ExecutorService executorService;

		clientRunners = new ConcurrentLinkedQueue<>();
		completedClientRunners = new ConcurrentLinkedQueue<>();
		executorService = Executors.newFixedThreadPool(numberOfConnectionsToTest);

		addClientsAndRunTest(numberOfConnectionsToTest, clientRunners, completedClientRunners, executorService);

		executorService.shutdown();

		while(!clientRunners.isEmpty()) {
			System.out.println(clientRunners.size() + " clients left...");
			try {
				executorService.awaitTermination(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Finished running load test for: " + numberOfConnectionsToTest + " connections");
	}

	private void addClientsAndRunTest(int numberOfConnectionsToTest,
									  ConcurrentLinkedQueue<ClientRunner> clientRunners,
									  ConcurrentLinkedQueue<ClientRunner> completedClientRunners,
									  ExecutorService executorService) {
		for(int i = 0; i < numberOfConnectionsToTest; i++) {
			ClientRunner clientRunner = new ClientRunner(i,
					clientRunners,
					completedClientRunners,
					clientTestResults,
					numberOfConnectionsToTest);
			clientRunners.add(clientRunner);
		}

		for(ClientRunner clientRunner : clientRunners) {
			executorService.execute(clientRunner);
		}
	}

}
