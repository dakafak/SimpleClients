package dev.fanger.simpleclients.examples.loadtest.results;

import dev.fanger.simpleclients.examples.loadtest.TestType;

import java.util.HashMap;
import java.util.Map;

public class TestResult {

	private int numberClientsDuringTest;
	private Map<TestType, Double> averageTimePerTestTime;

	public TestResult(int currentConnections) {
		this.numberClientsDuringTest = currentConnections;
		averageTimePerTestTime = new HashMap<>();
	}

	public int getNumberClientsDuringTest() {
		return numberClientsDuringTest;
	}

	public void setNumberClientsDuringTest(int numberClientsDuringTest) {
		this.numberClientsDuringTest = numberClientsDuringTest;
	}

	public Map<TestType, Double> getAverageTimePerTestTime() {
		return averageTimePerTestTime;
	}

	public void setAverageTimePerTestTime(Map<TestType, Double> averageTimePerTestTime) {
		this.averageTimePerTestTime = averageTimePerTestTime;
	}

}
