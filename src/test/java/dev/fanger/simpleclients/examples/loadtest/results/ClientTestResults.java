package dev.fanger.simpleclients.examples.loadtest.results;

import java.util.HashMap;
import java.util.LinkedHashMap;
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
		System.out.println("---------- Test Results CSV ----------");
		System.out.println("Test Type,Number of Connections,Run Time (ns)");

		LinkedHashMap<Integer, Map<TestType, Double>> numberClientsDuringTestToTotalTimeForTestType = new LinkedHashMap<>();
		Map<TestType, Integer> numberTestsPerTestType = new HashMap<>();

		for(TestResult testResult : testResults) {
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

				// Print data per test type
				StringBuilder dataLine = new StringBuilder();
				dataLine.append(testType.name());
				dataLine.append(",");
				dataLine.append(testResult.getNumberClientsDuringTest());
				dataLine.append(",");
				dataLine.append(time);
				System.out.println(dataLine.toString());
			}
		}

		System.out.println("---------------- End ----------------");

		for (TestType testType : TestType.values()) {
			double lastAverage = 0;

			for(Integer numberClientsDuringTest : numberClientsDuringTestToTotalTimeForTestType.keySet()) {
				Map<TestType, Double> totalTimeForEachTestType = numberClientsDuringTestToTotalTimeForTestType.get(numberClientsDuringTest);
				double averageTimeForTestType = totalTimeForEachTestType.get(testType) / numberTestsPerTestType.get(testType);
				if(lastAverage == 0) {
					lastAverage = averageTimeForTestType;
				}

				StringBuilder resultsForNumberOfClients = new StringBuilder();
				resultsForNumberOfClients.append("Number Clients: ");
				resultsForNumberOfClients.append(numberClientsDuringTest);

				resultsForNumberOfClients.append("\t| ");
				resultsForNumberOfClients.append(testType.name());
				resultsForNumberOfClients.append(": ");
				resultsForNumberOfClients.append(formattedTimeOutput(averageTimeForTestType));
				resultsForNumberOfClients.append("\t| Change: ");
				resultsForNumberOfClients.append(percentageChange(averageTimeForTestType, lastAverage));
				resultsForNumberOfClients.append("%");

				System.out.println(resultsForNumberOfClients.toString());

				lastAverage = averageTimeForTestType;
			}

			System.out.println("--------------------------------------------------");
		}
	}

	private String formattedTimeOutput(double numberToFormat) {
		return String.format(numberFormat, numberToFormat / 1_000_000d) + " ms";
	}

	private int percentageChange(double newValue, double oldValue) {
		return (int) (100 * ((newValue - oldValue) / oldValue));
	}

}
