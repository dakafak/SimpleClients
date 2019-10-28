package dev.fanger.simpleclients.examples.loadtest.results;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientTestResults {

	private ConcurrentLinkedQueue<TestResult> testResults;

	public ClientTestResults() {
		testResults = new ConcurrentLinkedQueue<>();
	}

	public void addTestResult(TestResult testResult) {
		testResults.add(testResult);
	}

	public void calculateAndPrintTestResults() {
//		Map<Integer, Map<TestType, List<Double>>> numberConnectionsToPrintableResults = new HashMap<>();
//
//		for(TestResult testResult : testResults) {
//			if(!numberConnectionsToPrintableResults.containsKey(testResult.getNumberClientsDuringTest())) {
//				numberConnectionsToPrintableResults.put(testResult.getNumberClientsDuringTest(), new HashMap<>());
//			}
//
//			Map<TestType, List<Double>> testTypeToAverageResults = numberConnectionsToPrintableResults.get(testResult.getNumberClientsDuringTest());
//			if(!testTypeToAverageResults.containsKey(testResult.getTestType())) {
//				testTypeToAverageResults.put(testResult.getTestType(), new LinkedList<>());
//			}
//
//			testTypeToAverageResults.get(testResult.getTestType()).add(testResult.getAverageTimeForTests());
//		}

		System.out.println("---------- Test Results CSV ----------");
		StringBuilder headerLine = new StringBuilder();
		headerLine.append("Number Connections,");
		for(int i = 0; i < TestType.values().length; i++) {
			headerLine.append(TestType.values()[i]);
			headerLine.append(" run time (ns)");
			if(i < TestType.values().length - 1) {
				headerLine.append(",");
			}
		}
		System.out.println(headerLine.toString());

		for(TestResult testResult : testResults) {
			StringBuilder dataLine = new StringBuilder();
			dataLine.append(testResult.getNumberClientsDuringTest());
			dataLine.append(",");
			for(int i = 0; i < TestType.values().length; i++) {
				Double time = testResult.getAverageTimePerTestTime().get(TestType.values()[i]);
				dataLine.append(time);
				if (i < TestType.values().length - 1) {
					dataLine.append(",");
				}
			}
			System.out.println(dataLine.toString());
		}

		System.out.println("---------------- End ----------------");
	}

}
