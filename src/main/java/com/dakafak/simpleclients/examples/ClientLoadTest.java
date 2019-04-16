package com.dakafak.simpleclients.examples;

import com.dakafak.simpleclients.connection.Connection;
import com.dakafak.simpleclients.examples.loadtest.ClientRunner;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientLoadTest {

	private int numberOfThreadsToUse = 4;
	private int clientsPerSessionForTest = 16;
	private int numberOfConnectionsToTest = 16;
	private int testsToRun = 4;
	private ConcurrentLinkedQueue<ClientRunner> clientRunners;
	private ConcurrentLinkedQueue<ClientRunner> completedClientRunners;
	private ExecutorService executorService;

	public ClientLoadTest() {
		clientRunners = new ConcurrentLinkedQueue<>();
		completedClientRunners = new ConcurrentLinkedQueue<>();
		executorService = Executors.newFixedThreadPool(numberOfThreadsToUse);

		for(int i = 0; i < testsToRun; i++) {
			System.out.println("Adding more clients: " + (i + 1) + "/" + testsToRun);
			addClientsAndRunTest();
		}

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
	}

	private void addClientsAndRunTest() {
		for(int i = 0; i < numberOfConnectionsToTest; i++) {
			Connection newConnection = Connection.newConnection("127.0.0.1", 1776);
			ClientRunner clientRunner = new ClientRunner(newConnection, i % clientsPerSessionForTest, clientRunners);
			clientRunners.add(clientRunner);
		}

		for(ClientRunner clientRunner : clientRunners) {
			executorService.execute(clientRunner);
		}
	}

}
