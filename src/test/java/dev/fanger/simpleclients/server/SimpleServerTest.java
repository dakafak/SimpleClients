package dev.fanger.simpleclients.server;

import dev.fanger.simpleclients.SimpleServer;
import dev.fanger.simpleclients.examples.SimpleServerExample;
import org.junit.Test;

public class SimpleServerTest {

    @Test
    public void shutDownTest() {
        SimpleServer simpleServer = (new SimpleServerExample()).getSimpleServer();
        simpleServer.shutDownServer();
    }

}
