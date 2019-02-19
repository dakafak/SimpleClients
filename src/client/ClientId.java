package client;

public class ClientId<T> {

    T id;

    public ClientId(T id){
        this.id = id;
    }

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }
}
