package ui;

import connection.Connection;
import connection.Id;
import server.ConnectionHandler;
import server.data.Payload;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionHandlerTester {

    public static void main(String[] args){
        System.out.println("-- Creating connection handlers");
        ConnectionHandler connectionHandler = new ConnectionHandler(5, 1776);

        System.out.println("-- Creating payload validator and connection success payload");
        connectionHandler.startListeningForConnections();
        connectionHandler.startValidatingClients();
        connectionHandler.startSendingDataToClients();

        while(true) {
            ConcurrentHashMap<Id, ConcurrentLinkedQueue<Payload>> inputPayloadQueuePerConnectionId = connectionHandler.getInputPayloadQueuePerConnectionId();

            for(Id id : inputPayloadQueuePerConnectionId.keySet()){
                Connection client = connectionHandler.getClient(id);
                if(client != null) {
                    connectionHandler.sendPayloadListToClient(client, inputPayloadQueuePerConnectionId.get(id));
                }
            }
        }

//        while(true){
//            ConcurrentHashMap<Id, Connection> allClients = connectionHandler.getClients();
//            if(allClients != null && !allClients.isEmpty()){
//                for(Connection connection : allClients.values()){
//
//                }
//            }
//        }


//
//        JFrame jFrame = new JFrame("");
//        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        jFrame.setPreferredSize(new Dimension(400, 200));
//        jFrame.setVisible(true);
    }

}
