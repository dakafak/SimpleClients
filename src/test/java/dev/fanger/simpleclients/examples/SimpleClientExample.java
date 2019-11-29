package dev.fanger.simpleclients.examples;

import dev.fanger.simpleclients.SimpleClient;
import dev.fanger.simpleclients.examples.client.tasks.bounce.BounceTask2;
import dev.fanger.simpleclients.examples.client.tasks.bounce.BounceTask4;
import dev.fanger.simpleclients.server.data.payload.Payload;

public class SimpleClientExample {

    private SimpleClient simpleClient;
    private boolean[] completedBounceTest;

    public SimpleClientExample() {
        completedBounceTest = new boolean[1];
        completedBounceTest[0] = false;

        simpleClient = new SimpleClient.Builder("127.0.0.1")
                .withPort(1776)
                .withTask("/test/bounce/2", new BounceTask2())
                .withTask("/test/bounce/4", new BounceTask4(completedBounceTest))
                .build();
    }

    public void runTest() {
        simpleClient.sendData(new Payload("Hey run the test", "/test/bounce/1"));

        while(!completedBounceTest[0]) { }

        System.out.println("Done");
    }

    public void shutDownClient() {
        simpleClient.shutDownClient();
    }

}
