package dev.fanger.simpleclients.examples.loadtest;

import dev.fanger.simpleclients.SimpleClient;
import dev.fanger.simpleclients.examples.server.data.Action;
import dev.fanger.simpleclients.server.data.payload.Payload;

public enum TestType {

    PING(500, (SimpleClient simpleClient) -> {
        Payload<String> testPayload = new Payload<>("Test Ping Payload ", "/test/ping");
        simpleClient.sendData(testPayload);
        simpleClient.retrieveData();
    }),
    ACTION(500, (SimpleClient simpleClient) -> {
        Payload<Action> newPayload = new Payload<>(Action.values()[(int)Math.floor(Math.random() * Action.values().length)], "/test/action");
        simpleClient.sendData(newPayload);
        simpleClient.retrieveData();
    });

    private int testsToRun;
    private Test test;

    TestType(int testsToRun, Test test) {
        this.testsToRun = testsToRun;
        this.test = test;
    }

    public int getTestsToRun() {
        return testsToRun;
    }

    public Test getTest() {
        return test;
    }

}
