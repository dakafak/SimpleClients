package server.data;

public class Payload<T> {

    T data;
    PayloadValidator<T> payloadValidator;

    public Payload(T data, PayloadValidator<T> payloadValidator){
        this.data = data;
        this.payloadValidator = payloadValidator;
    }

    public boolean isValid(){
        return payloadValidator.isValid(data);
    }

}
