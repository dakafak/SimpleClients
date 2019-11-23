package dev.fanger.simpleclients.examples;

import dev.fanger.simpleclients.SimpleClient;
import dev.fanger.simpleclients.examples.loadtest.Test;
import dev.fanger.simpleclients.examples.server.connection.ConnectionRequest;
import dev.fanger.simpleclients.examples.loadtest.results.TestResult;
import dev.fanger.simpleclients.examples.loadtest.TestType;
import dev.fanger.simpleclients.server.data.payload.Payload;

public class ClientExample {

    private SimpleClient simpleClient;
    private int sessionId;
    private TestResult testResult;

    public ClientExample(int sessionId,
                         int currentConnections) {
        this.sessionId = sessionId;
        simpleClient = new SimpleClient("127.0.0.1", 1776);
        testResult = new TestResult(currentConnections);
    }

    public void runTests() {
        connectToServer();

        for(TestType testType : TestType.values()) {
            runTest(testType.getTest(), testType);
        }
    }

    public void shutDownClientExample() {
        simpleClient.shutDownClient();
    }

    private void connectToServer() {
        ConnectionRequest connectionRequest = new ConnectionRequest("Test client " + simpleClient.getId(), sessionId);
        simpleClient.sendData(new Payload(connectionRequest, "/client/connect"));
    }

    private void runTest(Test test, TestType testType) {
        long totalIterationRunTime = 0;

        for(int i = 0; i < testType.getTestsToRun(); i++) {
            long startOfActionTest = System.nanoTime();

            test.runTest(simpleClient);

            long endOfActionTest = System.nanoTime();
            totalIterationRunTime += (endOfActionTest - startOfActionTest);
        }

        double averageIterationTime = totalIterationRunTime / (double) testType.getTestsToRun();
        testResult.getAverageTimePerTestTime().put(testType, averageIterationTime);
    }

    public TestResult getTestResult() {
        return testResult;
    }

}
