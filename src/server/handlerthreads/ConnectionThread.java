package server.handlerthreads;

import client.ClientConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class ConnectionThread implements Runnable {

    private int numberOfThreads;
    private int port;
    private boolean continueRunning;

    private ServerSocket serverSocket;

    private LinkedList<ClientConnection> clientsToVerify;

    public ConnectionThread(int numberOfThreads, int port){
        this.numberOfThreads = numberOfThreads;
        this.port = port;

        clientsToVerify = new LinkedList<>();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);

            while(continueRunning){
                Socket newClientSocket = serverSocket.accept();
                ClientConnection newClientConnection = new ClientConnection(newClientSocket);
                clientsToVerify.add(newClientConnection);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isContinueRunning() {
        return continueRunning;
    }

    public void setContinueRunning(boolean continueRunning) {
        this.continueRunning = continueRunning;
    }
}
