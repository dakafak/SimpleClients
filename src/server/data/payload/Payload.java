package server.data.payload;

import java.io.Serializable;

public class Payload<T> implements Serializable {

    private T data;
    private Enum payloadType;
    private PayloadValidator<T> payloadValidator;

    public Payload(T data, Enum payloadType, PayloadValidator<T> payloadValidator){
        this.data = data;
        this.payloadType = payloadType;
        this.payloadValidator = payloadValidator;
    }

    public boolean isValid(){
        return payloadValidator != null && payloadValidator.isValid(data);
    }

    @Override
    public String toString(){
        return data.toString();
    }

    public Enum getPayloadType() {
        return payloadType;
    }

}
