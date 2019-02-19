package server.handlerthreads;

import client.Client;
import client.ClientId;
import client.ClientValidator;
import server.data.Payload;
import server.data.PayloadValidator;
import server.data.payloads.ConnectionPayload;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionService implements Runnable {

    private int numberOfThreads;
    private int port;
    private boolean continueRunning;

    private ServerSocket serverSocket;

    private LinkedList<Client> clientsToValidate;
    private ConcurrentHashMap<ClientId, Client> allClientsReference;
    private ClientValidator clientValidator;
    private Payload connectionSuccessPayload;
    private PayloadValidator<ConnectionPayload> connectionPayloadValidator;

    public ConnectionService(int numberOfThreads,
                             int port,
                             ConcurrentHashMap<ClientId, Client> allClientsReference,
                             ClientValidator clientValidator,
                             Payload connectionSuccessPayload,
                             PayloadValidator<ConnectionPayload> connectionPayloadValidator){
        this.numberOfThreads = numberOfThreads;
        this.port = port;
        this.allClientsReference = allClientsReference;
        this.clientValidator = clientValidator;
        this.connectionSuccessPayload = connectionSuccessPayload;
        this.connectionPayloadValidator = connectionPayloadValidator;

        clientsToValidate = new LinkedList<>();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);

            while(continueRunning){
                Socket newClientSocket = serverSocket.accept();
                Client newClientConnection = new Client(newClientSocket);
                if(clientValidator.isValid(newClientConnection)) {
                    clientsToValidate.add(newClientConnection);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void validateNewClientsAndAddToAllClients(){
        for(Client client : clientsToValidate){
            client.sendData(connectionSuccessPayload);
            ConnectionPayload connectionPayload = (ConnectionPayload) client.retrieveData();

            if(connectionPayload != null && connectionPayloadValidator.isValid(connectionPayload)) {
                client.setId(connectionPayload.getClientId());
                allClientsReference.put(client.getId(), client);
            }

            clientsToValidate.remove(client);
        }
    }

    public boolean isContinueRunning() {
        return continueRunning;
    }

    public void setContinueRunning(boolean continueRunning) {
        this.continueRunning = continueRunning;
    }

}
