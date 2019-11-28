package dev.fanger.simpleclients.examples.loadtest;

import dev.fanger.simpleclients.TraditionalClient;
import dev.fanger.simpleclients.examples.server.data.Action;
import dev.fanger.simpleclients.server.data.payload.Payload;

public enum TestType {

    PING(500, (TraditionalClient traditionalClient) -> {
        traditionalClient.sendData(new Payload<>("Test Ping Payload ", "/test/ping"));
        traditionalClient.retrieveData();
    }),
    ACTION(500, (TraditionalClient traditionalClient) -> {
        traditionalClient.sendData(new Payload<>(Action.values()[(int)Math.floor(Math.random() * Action.values().length)], "/test/action"));
        traditionalClient.retrieveData();
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
