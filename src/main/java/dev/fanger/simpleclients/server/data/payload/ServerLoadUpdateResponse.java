package dev.fanger.simpleclients.server.data.payload;

import dev.fanger.simpleclients.server.ServerConnectionInfo;

import java.io.Serializable;

public class ServerLoadUpdateResponse implements Serializable {

    private Integer load;
    private Class taskClass;
    private ServerConnectionInfo serverConnectionInfo;

    public ServerLoadUpdateResponse(Integer load, Class taskClass, ServerConnectionInfo serverConnectionInfo) {
        this.load = load;
        this.taskClass = taskClass;
        this.serverConnectionInfo = serverConnectionInfo;
    }

    public Integer getLoad() {
        return load;
    }

    public void setLoad(Integer load) {
        this.load = load;
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
