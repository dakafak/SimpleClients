package com.dakafak.simpleclients.connection;

import com.dakafak.simpleclients.server.data.payload.Payload;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Connection {

    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Id id;
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

    synchronized public void sendData(Payload payload){
        try {
            outputStream.writeObject(payload);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Payload retrieveData(){
        try {
            Payload readObject = (Payload) inputStream.readObject();
            return readObject;
        } catch (Exception e) {
            clientShouldBeDestroyed = true;
            e.printStackTrace();
        }

        return null;
    }

    public boolean clientShouldBeDestroyed() {
        return clientShouldBeDestroyed;
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
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
