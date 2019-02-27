package server.data.payload;

import java.io.Serializable;

abstract public class PayloadValidator<T> implements Serializable {

    public abstract boolean isValid(T data);

}
