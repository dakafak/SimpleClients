package dev.fanger.simpleclients.server;

public class ServerConnectionInfo {

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

}
