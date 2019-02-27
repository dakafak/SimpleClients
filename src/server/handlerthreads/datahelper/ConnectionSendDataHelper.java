package server.handlerthreads.datahelper;

import server.data.payload.Payload;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionSendDataHelper implements Runnable {

    private boolean continueRunning;
    ConcurrentLinkedQueue<Payload> payloadsToSend;
    private ObjectOutputStream outputStream;

    public ConnectionSendDataHelper(Socket connection) {
        payloadsToSend = new ConcurrentLinkedQueue<>();
        try {
            outputStream = new ObjectOutputStream(connection.getOutputStream());
        } catch (IOException e) {
            continueRunning = false;
            e.printStackTrace();
        }
    }

    public void addPayloadToSend(Payload payload) {
        if(payload.isValid()) {
            payloadsToSend.add(payload);
        }
    }

    @Override
    public void run() {
        while(continueRunning) {
            if(!payloadsToSend.isEmpty()) {
                try {
                    outputStream.writeObject(payloadsToSend.poll());
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setContinueRunning(boolean continueRunning) {
        this.continueRunning = continueRunning;
    }

}
