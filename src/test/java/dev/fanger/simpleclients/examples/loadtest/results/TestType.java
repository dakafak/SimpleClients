package dev.fanger.simpleclients.examples.loadtest.results;

public enum TestType {
	PING(50),
	ACTION(50);

	private int testsToRun;

	TestType(int testsToRun) {
		this.testsToRun = testsToRun;
	}

	public int getTestsToRun() {
		return testsToRun;
	}

}
