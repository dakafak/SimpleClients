package dev.fanger.simpleclients.examples.loadtest.results;

import dev.fanger.simpleclients.examples.loadtest.TestType;

import java.util.HashMap;
import java.util.Map;

public class PrintableTimeResults {

	int numberClients;
	Map<TestType, Double> timeResultsForType;

	public PrintableTimeResults(int numberClients) {
		this.numberClients = numberClients;
		timeResultsForType = new HashMap<>();
	}

	public void addTestTime(TestType testType, Double time) {
		timeResultsForType.put(testType, time);
	}

	public double getAverageTimeForTestType(TestType testType) {
		if(!timeResultsForType.containsKey(testType)) {
			return 0;
		}

		return timeResultsForType.get(testType);
	}

}
