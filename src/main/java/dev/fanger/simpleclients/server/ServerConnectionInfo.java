package dev.fanger.simpleclients.server;

import java.io.Serializable;

public class ServerConnectionInfo implements Serializable {

    private String ip;
    private int port;

    public ServerConnectionInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return ip + ":" + port;
    }

}
