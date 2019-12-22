package dev.fanger.simpleclients.server.data.task;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.handlerthreads.datahelper.DataReceiveHelper;

public abstract class Task {

    private String url;
    private boolean allowCloudProcessing;
    private boolean requiresReturnData;
    private int maxLoadForCloud;

    /**
     * When a payload is retrieved by {@link DataReceiveHelper} it will call this method immediately
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

    public boolean isRequiresReturnData() {
        return requiresReturnData;
    }

    public void setRequiresReturnData(boolean requiresReturnData) {
        this.requiresReturnData = requiresReturnData;
    }

    public int getMaxLoadForCloud() {
        return maxLoadForCloud;
    }

    public void setMaxLoadForCloud(int maxLoadForCloud) {
        this.maxLoadForCloud = maxLoadForCloud;
    }

}
