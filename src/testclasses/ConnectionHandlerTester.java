package testclasses;

import server.ConnectionHandler;

public class ConnectionHandlerTester {

    public static void main(String[] args){
        System.out.println("-- Creating connection handlers");
        ConnectionHandler connectionHandler = new ConnectionHandler(1776);
        connectionHandler.startTaskExecutorService(5, 1000);
        connectionHandler.addTask(MyPayloadTypes.PING, new PingTask());
        connectionHandler.startListeningForConnections();
        connectionHandler.startValidatingClients();
        connectionHandler.startSendingDataToClients();
    }

}
