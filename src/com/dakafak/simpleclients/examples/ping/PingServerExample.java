package com.dakafak.simpleclients.examples.ping;

import com.dakafak.simpleclients.examples.MyPayloadTypes;
import com.dakafak.simpleclients.server.ConnectionHandler;

public class PingServerExample {

    public static void main(String[] args){
        ConnectionHandler connectionHandler = new ConnectionHandler(1776);
        connectionHandler.addTask(MyPayloadTypes.PING, new PingTask());
        connectionHandler.startListeningForConnections();
    }

}
