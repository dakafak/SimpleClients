package ui;

import server.ConnectionHandler;
import server.data.Payload;
import server.data.PayloadValidator;

public class ConnectionHandlerTester {

    public static void main(String[] args){
        System.out.println("-- Creating connection handlers");
        ConnectionHandler connectionHandler = new ConnectionHandler(5, 1776);

        System.out.println("-- Creating payload validator and connection success payload");
        PayloadValidator payloadValidator = new TestPayloadValidator();
        Payload<String> connectionSuccessPayload = new Payload<>("Connection Successful", payloadValidator);
        connectionHandler.startListeningForConnections(connectionSuccessPayload);
        connectionHandler.startRecievingDataFromClients();
        connectionHandler.startSendingDataToClients();

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
