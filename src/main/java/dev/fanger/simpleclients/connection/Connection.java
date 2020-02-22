package dev.fanger.simpleclients.connection;

import dev.fanger.simpleclients.logging.Level;
import dev.fanger.simpleclients.logging.Logger;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.task.Task;

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
    private boolean connectionShouldBeDestroyed;

    public Connection(Socket socket){
        this.socket = socket;

        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            Logger.log(Level.ERROR, e);
            shutDownConnection();
        }

        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            Logger.log(Level.ERROR, e);
            shutDownConnection();
        }
    }

    /**
     * Send specified {@link Payload}
     * synchronized for passive {@link Task} or for forceful {@link Payload} send
     *
     * @param payload
     */
    synchronized public void sendData(Payload payload){
        if(!connectionShouldBeDestroyed) {
            try {
                outputStream.writeObject(payload);
                outputStream.flush();
            } catch (IOException e) {
                Logger.log(Level.DEBUG, e);
                shutDownConnection();
            }
        }
    }

    /**
     * Wait for data. You cannot try retrieving data if you also have passive tasks checking for data
     * They will block each other
     *
     * @return
     */
    public Payload retrieveData(){
        if(!connectionShouldBeDestroyed) {
            try {
                Payload readObject = (Payload) inputStream.readObject();
                return readObject;
            } catch (Exception e) {
                Logger.log(Level.DEBUG, e);
                shutDownConnection();
            }
        }

        return null;
    }

    /**
     * Shutdown connection
     */
    public void shutDownConnection() {
        connectionShouldBeDestroyed = true;

        if(inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                Logger.log(Level.DEBUG, e);
            }
        }

        if(outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                Logger.log(Level.DEBUG, e);
            }
        }

        try {
            socket.close();
        } catch (IOException e) {
            Logger.log(Level.DEBUG, e);
        }
    }

    /**
     * Creates a new connection for the specified hostname and port
     *
     * @param hostname
     * @param port
     * @return
     */
    public static Connection newClientConnection(String hostname, int port) {
        Socket clientSocket = new Socket();

        try {
            clientSocket.connect(new InetSocketAddress(hostname, port));
            Connection connection = new Connection(clientSocket);
            return connection;
        } catch (IOException e) {
            Logger.log(Level.ERROR, e);
        }

        return null;
    }

    /**
     *
     * @return true if this connection should be shutdown
     */
    public boolean connectionShouldBeDestroyed() {
        return connectionShouldBeDestroyed;
    }

    /**
     *
     * @return UUID for this connection
     */
    public UUID getId() {
        return id;
    }

}
