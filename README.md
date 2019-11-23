# SimpleClients
SimpleClients is a tool to make server and client socket connections easier to setup and maintain - by handling 
concurrent data handling for you!

## Examples

### Creating a server
Creating a server uses a builder pattern. You simply define your port and add any tasks you want your server to run!
```java
public class ServerExample {

    public ServerExample() {
        SimpleServer simpleServer = new SimpleServer.Builder(1776)
                .withLoggingType(SystemPrintTimeLogger.class)
                .withTask("/test/ping", new PingTask())
                .build();

        simpleServer.startListeningForConnections();
    }

}
```

### What are tasks?
A task is a passive connection handled by either a server or client. The task will continuously run in the background 
and handle any data that hits the specified "URL"

In the above example, we add a ping task to the server, below is how that task is defined.
```java
public class PingTask extends Task {

    @Override
    public void executeTask(Connection connection, Payload payload) {
        connection.sendData(payload);
    }

}
```
That's it! It's incredibly easy to create and add tasks to a SimpleClients server. All you need to worry about, is 
defining **what** to do with received data, rather than worrying about **how** to receieve data.
In the above `PingTask`, data is sent directly back to the client from which the data was received.

### How do I send data to the server?
By creating a SimpleClient! :)
```java
public class ClientExample {

    public ClientExample() {
        SimpleClient simpleClient = new SimpleClient("127.0.0.1", 1776);
        simpleClient.sendData(new Payload<>("Test Ping Payload ", "/test/ping"));
    }
    
}
```
That's it! Just define your ip and port, then send whatever data you'd like to the server. The `Payload` will contain 
both the data you're sending as well as the endpoint you'd like to hit. The server will use this information to 
determine what task to use to handle your request.

### What do I do when I'm done? How do I shut down the server and client?
For the server:
```java
simpleServer.shutDownServer();
```
For the client:
```java
simpleClient.shutDownClient();
```

That's it! Full examples and a load test can be found in
`src/test/java/dev/fanger/simpleclients`