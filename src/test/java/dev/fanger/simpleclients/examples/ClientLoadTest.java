package dev.fanger.simpleclients.examples;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.examples.loadtest.ClientExample;
import dev.fanger.simpleclients.examples.loadtest.ClientRunner;
import dev.fanger.simpleclients.examples.loadtest.results.ClientTestResults;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientLoadTest {

//	private int numberOfThreadsToUse = 4;
	private int maxConnectionsToTest = 128;// Tests are run *=2 up to max number of tests to run
	private int pingTestsToRun = 50;
	private int actionTestsToRun = 50;

	private ClientTestResults clientTestResults;

	public ClientLoadTest() {
		clientTestResults = new ClientTestResults();

		for(int i = 1; i <= maxConnectionsToTest; i *= 2) {
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

//		for(int i = 0; i < testsToRun; i++) {
//			System.out.println("Adding more clients: " + (i + 1) + "/" + testsToRun);
			addClientsAndRunTest(numberOfConnectionsToTest, clientRunners, completedClientRunners, executorService);
//		}

		executorService.shutdown();

		while(!clientRunners.isEmpty()) {
			System.out.println(clientRunners.size() + " clients left...");
			try {
				executorService.awaitTermination(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Finished running load test.");
//		printResultsFromTest(completedClientRunners);
	}

	private void addClientsAndRunTest(int numberOfConnectionsToTest,
									  ConcurrentLinkedQueue<ClientRunner> clientRunners,
									  ConcurrentLinkedQueue<ClientRunner> completedClientRunners,
									  ExecutorService executorService) {
		for(int i = 0; i < numberOfConnectionsToTest; i++) {
			Connection newConnection = Connection.newConnection("127.0.0.1", 1776);
			ClientRunner clientRunner = new ClientRunner(newConnection,
					i,
					clientRunners,
					completedClientRunners,
					pingTestsToRun,
					actionTestsToRun,
					clientTestResults,
					numberOfConnectionsToTest);
			clientRunners.add(clientRunner);
		}

		for(ClientRunner clientRunner : clientRunners) {
			executorService.execute(clientRunner);
		}
	}

//	private void printResultsFromTest(ConcurrentLinkedQueue<ClientRunner> completedClientRunners) {
//		List<ClientRunner> sortedClientRunners = new LinkedList<>();
//		sortedClientRunners.addAll(completedClientRunners);
//		sortedClientRunners.sort((clientExample1, clientExample2) -> {
//			if(
//					(clientExample1.getClientExample().getAveragePingTimeInNanoSeconds()
//							+ clientExample1.getClientExample().getAverageActionTestTimeInNanoSeconds())
//			 	>=
//					(clientExample2.getClientExample().getAveragePingTimeInNanoSeconds()
//							+ clientExample2.getClientExample().getAverageActionTestTimeInNanoSeconds())) {
//				return 1;
//			} else {
//				return -1;
//			}
//		});
//
//		for(ClientRunner clientRunner : sortedClientRunners) {
//			ClientExample clientExample = clientRunner.getClientExample();
//			double pingAvarage = clientExample.getAveragePingTimeInNanoSeconds();
//			double actionAvarage = clientExample.getAverageActionTestTimeInNanoSeconds();
//			double totalAverage = pingAvarage + actionAvarage;
//			System.out.println(clientExample.getConnection().getId() +
//					" | ping avg: " + pingAvarage + " ns\t\t" +
//					" | action avg: " + actionAvarage + " ns\t\t" +
//					" | total avg: " + totalAverage + " ns"
//			);
//		}
//	}

}
