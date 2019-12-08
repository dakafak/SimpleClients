package dev.fanger.simpleclients.server.data.task;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.server.cloud.ReturnType;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.handlerthreads.datahelper.ConnectionReceiveDataHelper;

public abstract class Task {

    private String url;
    private boolean allowCloudProcessing;
    private ReturnType returnType;

    /**
     * When a payload is retrieved by {@link ConnectionReceiveDataHelper} it will call this method immediately
     *
     * @param connection
     * @param payload
     */
    public abstract void executeTask(Connection connection, Payload payload);

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setAllowCloudProcessing(boolean allowCloudProcessing) {
        this.allowCloudProcessing = allowCloudProcessing;
    }

    public boolean hasAllowedCloudProcessing() {
        return allowCloudProcessing;
    }

    public ReturnType getReturnType() {
        return returnType;
    }

    public void setReturnType(ReturnType returnType) {
        this.returnType = returnType;
    }

}
