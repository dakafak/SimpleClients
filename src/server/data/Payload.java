package server.data;

import java.io.Serializable;

public class Payload<T> implements Serializable {

    T data;
    PayloadValidator<T> payloadValidator;

    public Payload(T data, PayloadValidator<T> payloadValidator){
        this.data = data;
        this.payloadValidator = payloadValidator;
    }

    public boolean isValid(){
        return payloadValidator != null && payloadValidator.isValid(data);
    }

}
