package com.simpleclients.server.data.payload;

import java.io.Serializable;

public class Payload<T> implements Serializable {

    private T data;
    private Enum payloadType;

    public Payload(T data, Enum payloadType){
        this.data = data;
        this.payloadType = payloadType;
    }

    @Override
    public String toString(){
        return data.toString();
    }

    public Enum getPayloadType() {
        return payloadType;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setPayloadType(Enum payloadType) {
        this.payloadType = payloadType;
    }

}
