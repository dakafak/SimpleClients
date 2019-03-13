package com.dakafak.simpleclients.examples;

import com.dakafak.simpleclients.server.ConnectionHandler;

public class ServerExample {

    public static void main(String[] args){
        System.out.println("-- Creating com.dakafak.simpleclients.connection handlers");
        ConnectionHandler connectionHandler = new ConnectionHandler(1776);
        connectionHandler.addTask(MyPayloadTypes.PING, new PingTask());
        connectionHandler.startListeningForConnections();
    }

}
