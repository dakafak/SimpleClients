package com.simpleclients.server.data.datatypes;

public class ConnectionData<T> {

    T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
