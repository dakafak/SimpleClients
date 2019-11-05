package dev.fanger.simpleclients.examples.loadtest.results;

import java.util.HashMap;
import java.util.LinkedHashMap;
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

	private String numberFormat = "%6.3e";

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

		LinkedHashMap<Integer, Map<TestType, Double>> numberClientsDuringTestToTotalTimeForTestType = new LinkedHashMap<>();
		Map<TestType, Integer> numberTestsPerTestType = new HashMap<>();

		for(TestResult testResult : testResults) {
			StringBuilder dataLine = new StringBuilder();
			dataLine.append(testResult.getNumberClientsDuringTest());
			dataLine.append(",");
			for(int i = 0; i < TestType.values().length; i++) {
				TestType testType = TestType.values()[i];
				Double time = testResult.getAverageTimePerTestTime().get(testType);

				// Add number of specific test type ran
				if(!numberTestsPerTestType.containsKey(testType)) {
					numberTestsPerTestType.put(testType, 0);
				}
				numberTestsPerTestType.put(testType, numberTestsPerTestType.get(testType) + 1);

				// Add time to total test time map
				if(!numberClientsDuringTestToTotalTimeForTestType.containsKey(testResult.getNumberClientsDuringTest())) {
					numberClientsDuringTestToTotalTimeForTestType.put(testResult.getNumberClientsDuringTest(), new HashMap<>());
				}
				Map<TestType, Double> totalTimeForEachTestType = numberClientsDuringTestToTotalTimeForTestType.get(testResult.getNumberClientsDuringTest());
				if(!totalTimeForEachTestType.containsKey(testType)) {
					totalTimeForEachTestType.put(testType, 0d);
				}
				totalTimeForEachTestType.put(testType, totalTimeForEachTestType.get(testType) + time);

				// Build line for individual results
				dataLine.append(time);
				if (i < TestType.values().length - 1) {
					dataLine.append(",");
				}
			}
			System.out.println(dataLine.toString());
		}

		System.out.println("---------------- End ----------------");

		for(Integer numberClientsDuringTest : numberClientsDuringTestToTotalTimeForTestType.keySet()) {
			StringBuilder resultsForNumberOfClients = new StringBuilder();
			resultsForNumberOfClients.append("Number Clients: ");
			resultsForNumberOfClients.append(numberClientsDuringTest);

			double totalAverage = 0;

			Map<TestType, Double> totalTimeForEachTestType = numberClientsDuringTestToTotalTimeForTestType.get(numberClientsDuringTest);
			for (TestType testType : totalTimeForEachTestType.keySet()) {
				double averageTimeForTestType = totalTimeForEachTestType.get(testType) / numberTestsPerTestType.get(testType);
				totalAverage += averageTimeForTestType;

				resultsForNumberOfClients.append("\t| ");
				resultsForNumberOfClients.append(testType.name());
				resultsForNumberOfClients.append(": ");
				resultsForNumberOfClients.append(formattedTimeOutput(averageTimeForTestType));
			}

			totalAverage = totalAverage / TestType.values().length;

			resultsForNumberOfClients.append("\t| Total Average: ");
			resultsForNumberOfClients.append(formattedTimeOutput(totalAverage));

			System.out.println(resultsForNumberOfClients.toString());
		}
	}

	private String formattedTimeOutput(double numberToFormat) {
		return String.format(numberFormat, numberToFormat / 1_000_000d) + " ms";
	}

}
