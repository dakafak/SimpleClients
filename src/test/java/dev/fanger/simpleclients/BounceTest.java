package dev.fanger.simpleclients;

import dev.fanger.simpleclients.examples.SimpleClientExample;
import dev.fanger.simpleclients.examples.SimpleServerExample;

public class BounceTest {

    public static void main(String[] args) {
        new BounceTest();
    }

    public BounceTest() {
        SimpleServerExample simpleServerExample = new SimpleServerExample();
        SimpleClientExample simpleClientExample = new SimpleClientExample();

        simpleClientExample.runTest();

        simpleClientExample.shutDownClient();
        simpleServerExample.shutdownServer();
    }

}
