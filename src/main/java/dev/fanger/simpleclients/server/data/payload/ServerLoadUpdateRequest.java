package dev.fanger.simpleclients.server.data.payload;

import dev.fanger.simpleclients.server.ServerConnectionInfo;

import java.io.Serializable;

public class ServerLoadUpdateRequest implements Serializable {

    private Class taskClass;
    private ServerConnectionInfo serverConnectionInfo;

    public ServerLoadUpdateRequest(Class taskClass, ServerConnectionInfo serverConnectionInfo) {
        this.taskClass = taskClass;
        this.serverConnectionInfo = serverConnectionInfo;
    }

    public Class getTaskClass() {
        return taskClass;
    }

    public void setTaskClass(Class taskClass) {
        this.taskClass = taskClass;
    }

    public ServerConnectionInfo getServerConnectionInfo() {
        return serverConnectionInfo;
    }

    public void setServerConnectionInfo(ServerConnectionInfo serverConnectionInfo) {
        this.serverConnectionInfo = serverConnectionInfo;
    }

}
