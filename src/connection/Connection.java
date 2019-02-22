package connection;

import server.data.Payload;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {

    private Socket connection;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Id id;
    private boolean clientShouldBeDestroyed;

    public Connection(Socket connection){
        this.connection = connection;
    }

    public void sendData(Payload payload){
        try {
            if(outputStream == null){
                outputStream = new ObjectOutputStream(connection.getOutputStream());
            }

            outputStream.writeObject(payload);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Payload retrieveData(){
        try {
            if(inputStream == null){
                inputStream = new ObjectInputStream(connection.getInputStream());
            }

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

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }
}
