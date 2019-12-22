package dev.fanger.simpleclients.server.cloud;

import java.io.Serializable;

public class TaskLoad implements Serializable {

    private int load;

    public synchronized void incrementTaskLoad() {
        load++;
    }

    public synchronized void decrementTaskLoad() {
        load--;
    }

    public synchronized int getCurrentTaskLoad() {
        return load;
    }

    @Override
    public String toString() {
        return String.valueOf(load);
    }

}
