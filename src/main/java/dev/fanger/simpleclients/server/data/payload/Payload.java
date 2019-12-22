package dev.fanger.simpleclients.server.data.payload;

import java.io.Serializable;

public class Payload<T extends Serializable> implements Serializable {

    private T data;
    private String payloadUrl;

    public Payload(T data, String payloadUrl){
        this.data = data;
        this.payloadUrl = payloadUrl;
    }

    @Override
    public String toString(){
        return data.toString();
    }

    public String getPayloadUrl() {
        return payloadUrl;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setPayloadUrl(String payloadUrl) {
        this.payloadUrl = payloadUrl;
    }

}
