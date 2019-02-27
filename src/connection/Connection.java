package connection;

import server.data.payload.Payload;
import server.handlerthreads.datahelper.ConnectionSendDataHelper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class Connection {

    private Socket socket;
    private ObjectInputStream inputStream;
    private Id id;
    private boolean clientShouldBeDestroyed;
    private ConnectionSendDataHelper connectionSendDataHelper;

    public Connection(Socket socket){
        this.socket = socket;
        connectionSendDataHelper = new ConnectionSendDataHelper(socket);
        connectionSendDataHelper.setContinueRunning(true);
        Thread connectionSendDataHelperThread = new Thread(connectionSendDataHelper);
        connectionSendDataHelperThread.start();

        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            clientShouldBeDestroyed = true;
            e.printStackTrace();
        }
    }

    public void sendData(Payload payload){
        connectionSendDataHelper.addPayloadToSend(payload);
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
}
