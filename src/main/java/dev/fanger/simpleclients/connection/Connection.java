package dev.fanger.simpleclients.connection;

import dev.fanger.simpleclients.server.data.payload.Payload;

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
            e.printStackTrace();
            shutDownClient();
        }

        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            shutDownClient();
        }
    }

    synchronized public void sendData(Payload payload){
        if(!clientShouldBeDestroyed) {
            try {
                outputStream.writeObject(payload);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
                shutDownClient();
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
                shutDownClient();
            }
        }

        return null;
    }

    public boolean clientShouldBeDestroyed() {
        return clientShouldBeDestroyed;
    }

    public void shutDownClient() {
        if(inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        clientShouldBeDestroyed = true;
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
