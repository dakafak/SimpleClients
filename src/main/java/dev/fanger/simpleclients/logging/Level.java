package dev.fanger.simpleclients.logging;

public enum Level {

    /**
     * Information about general processes during operation like when a client connects
     */
    INFO,

    /**
     * A non-critical issue that occured. Generally when a client is disconnected because of an exception
     */
    WARN,

    /**
     * Generally any critical errors like sockets unable to be opened
     */
    ERROR,

    /**
     * Additional information that isn't necessary for traditional use - as it would flood logs
     */
    DEBUG

}
