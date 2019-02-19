package server.data;

abstract public class PayloadValidator<T> {

    public abstract boolean isValid(T data);

}
