package com.dakafak.simpleclients.connection;

import com.dakafak.simpleclients.server.data.payload.Payload;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;

public class Connection {

    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private UUID id = UUID.randomUUID();
    private boolean clientShouldBeDestroyed;

    public Connection(Socket socket){
        this.socket = socket;

        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            clientShouldBeDestroyed = true;
            e.printStackTrace();
        }

        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            clientShouldBeDestroyed = true;
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized public void sendData(Payload payload){
        if(!clientShouldBeDestroyed) {
            try {
                outputStream.writeObject(payload);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
                clientShouldBeDestroyed = true;
            }
        }
    }

    public Payload retrieveData(){
        if(!clientShouldBeDestroyed) {
            try {
                Payload readObject = (Payload) inputStream.readObject();
                return readObject;
            } catch (Exception e) {
                e.printStackTrace();
                clientShouldBeDestroyed = true;
            }
        }

        return null;
    }

    public boolean clientShouldBeDestroyed() {
        return clientShouldBeDestroyed;
    }

    public UUID getId() {
        return id;
    }

    public static Connection newConnection(String hostname, int port) {
        Socket clientSocket = new Socket();

        try {
            clientSocket.connect(new InetSocketAddress(hostname, port));
            Connection connection = new Connection(clientSocket);
            return connection;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
