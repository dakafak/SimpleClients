package client;

import server.data.Payload;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {

    private Socket clientSocket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private ClientId id;
    private boolean clientShouldBeDestroyed;

    public Client(Socket clientSocket){
        this.clientSocket = clientSocket;

        try {
            this.outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            clientShouldBeDestroyed = true;
            e.printStackTrace();
        }

        try {
            this.inputStream = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            clientShouldBeDestroyed = true;
            e.printStackTrace();
        }
    }

    public void sendData(Payload payload){
        try {
            outputStream.writeObject(payload);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Payload retrieveData(){
        try {
            return (Payload) inputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ClassCastException e){
            e.printStackTrace();
        }

        return null;
    }

    public boolean clientShouldBeDestroyed() {
        return clientShouldBeDestroyed;
    }

    public ClientId getId() {
        return id;
    }

    public void setId(ClientId id) {
        this.id = id;
    }
}
