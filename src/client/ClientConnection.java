package client;

import java.net.Socket;

public class ClientConnection {

    Socket clientSocket;

    public ClientConnection(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

}
