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
                .withTask("/client/connect", new ConnectionTask(sessionIdToUsers, connectionIdToSessionId))
                .withTask("/test/ping", new PingTask())
                .withTask("/test/action", new ActionTask(actionRecords, sessionIdToUsers, connectionIdToSessionId))
                .withTask("/test/bounce/1", new BounceTask1())
                .withTask("/test/bounce/3", new BounceTask3())
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
    public void executePayload(Connection connection, Payload payload) {
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
public class SimpleClientExample {

    public SimpleClientExample() {
        SimpleClient simpleClient = new SimpleClient.Builder("127.0.0.1", 1776)
                .withTask("/test/bounce/2", new BounceTask2())
                .withTask("/test/bounce/4", new BounceTask4(completedBounceTest))
                .build();
        simpleClient.sendData(new Payload("Hey run the test", "/test/bounce/1"));
    }
    
}
```
That's it! Just define your ip and port, then send whatever data you'd like to the server. The `Payload` will contain 
both the data you're sending as well as the endpoint you'd like to hit. The server will use this information to 
determine what task to use to handle your request.

### What if I want to manually retrieve data?
You can do so with a TraditionalClient!
```java
public class TraditionClientExample {

    public TraditionClientExample() {
        TraditionalClient traditionalClient = new TraditionalClient("127.0.0.1", 1776);
        traditionalClient.sendData(new Payload<>("Test Ping Payload ", "/test/ping"));
        System.out.println(traditionalClient.retrieveData());
    }
    
}
```

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
