package dev.fanger.simpleclients;

import dev.fanger.simpleclients.examples.SimpleClientExample;
import dev.fanger.simpleclients.examples.SimpleServerExample;
import dev.fanger.simpleclients.examples.TraditionClientExample;
import dev.fanger.simpleclients.server.data.payload.Payload;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SimpleClientsExamplesTest {

    private SimpleServer simpleServer;
    private SimpleClient simpleClient;
    private TraditionalClient traditionalClient;

    @Before
    public void setup() {
        simpleServer = (new SimpleServerExample()).getSimpleServer();
        simpleClient = (new SimpleClientExample()).getSimpleClient();
        traditionalClient = (new TraditionClientExample(1, 0)).getTraditionalClient();
    }

    @Test
    public void testSimpleServer() {
        assertEquals(1776, simpleServer.getPort());
        assertNotNull(simpleServer.getClients());
        assertNotNull(simpleServer.getTasks());
    }

    @Test
    public void testSimpleClient() {
        assertNotNull(simpleClient.getId());
        assertNotNull(simpleClient.getTasks());
    }

    @Test
    public void testTraditionalClient() {
        assertNotNull(traditionalClient.getId());
        Payload<String> pingPayload = new Payload<>("ayyo", "/test/ping");
        traditionalClient.sendData(pingPayload);
        assertEquals(pingPayload.getData(), traditionalClient.retrieveData().getData());
    }

}
