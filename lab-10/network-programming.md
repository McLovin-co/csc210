# Network Programming

## 1. Overview of Network Programming in Java

Network programming in Java typically relies on the **java.net** package, which provides classes for low-level network communications, such as sockets, URLs, and server sockets. The goal is to enable two or more applications to communicate over a network—whether in a traditional client-server architecture or in a decentralized peer-to-peer setup.

---

## 2. Client-Server Programming

### 2.1. Concept

In a client-server model, one or more clients request resources or services, and a central server responds. For applications that “see the same thing,” the server often serves as the authoritative source of state, ensuring all connected clients are synchronized.

### 2.2. Basic Components

- **ServerSocket (Server Side):** Listens for incoming client connections.
- **Socket (Client Side):** Connects to the server.
- **Input/Output Streams:** Handle data transfer between the server and clients.

### 2.3. Simple Example

#### Server Code Example

```java
import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Server is listening on port 5000");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                new ClientHandler(socket).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
        ) {
            String text;
            while ((text = reader.readLine()) != null) {
                System.out.println("Received: " + text);
                // Echo the message back to the client
                writer.println("Server: " + text);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
```

#### Client Code Example

```java
import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000)) {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            // A background thread reads replies from the server and prints them.
            // Without this thread the client would only write and never see any
            // server responses (including the echo the server sends back).
            Thread readerThread = new Thread(() -> {
                try (BufferedReader serverIn = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()))) {
                    String line;
                    while ((line = serverIn.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException ex) {
                    // Server closed the connection — exit quietly.
                }
            });
            readerThread.setDaemon(true); // exits when the main thread exits
            readerThread.start();

            // Main thread reads from the console and sends to the server.
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            String text;
            while ((text = consoleReader.readLine()) != null) {
                writer.println(text);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
```

**Key Points:**
- **Server:** Uses `ServerSocket` to accept connections and creates a new thread for each client.
- **Client:** Connects to the server’s IP and port using a `Socket`.
- **Two threads needed:** The client requires a dedicated reader thread to receive server replies; using only one thread would block on console input and never process incoming messages.
- **Data Exchange:** Utilizes streams to send and receive messages, making it possible to keep the state synchronized.

### 2.4. Coordinating Shared State

When two applications need to see the same thing:
- The server maintains the authoritative state.
- Clients send updates to the server, which then broadcasts the updated state.
- Consider using a dedicated protocol or JSON/XML messages for structured communication.
- Use multi-threading on the server to handle simultaneous client connections.

---

## 3. Peer-to-Peer (P2P) Implementation

### 3.1. Concept

In a P2P network, every node (or peer) acts as both a client and a server. Instead of relying on a single centralized server, peers communicate directly with one another to synchronize state.

### 3.2. Basic Components

- **Socket:** Each peer opens a `ServerSocket` to accept incoming connections.
- **Client Socket:** Each peer also initiates connections to other peers.
- **Discovery:** Mechanism for peers to find each other (via a central directory, multicast, or broadcast).
- **State Synchronization:** Protocol for ensuring all peers have the same view of the data.

### 3.3. Simple P2P Example

Below is a simplified example where each peer can both send and receive messages.

#### Peer Code Example

```java
import java.io.*;
import java.net.*;

public class Peer {
    private int port;
    
    public Peer(int port) {
        this.port = port;
    }
    
    public void start() {
        new Thread(() -> listenForPeers()).start();
        listenToUserInput();
    }
    
    private void listenForPeers() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Peer listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleIncomingConnection(socket)).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void handleIncomingConnection(Socket socket) {
        try (
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        ) {
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Received from peer: " + message);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void listenToUserInput() {
        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            String message;
            while ((message = consoleReader.readLine()) != null) {
                // For demonstration, assume peer connects to a known port (e.g., 6000)
                sendMessage("localhost", 6000, message);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void sendMessage(String host, int port, String message) {
        try (Socket socket = new Socket(host, port)) {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(message);
        } catch (IOException ex) {
            System.out.println("Could not connect to peer at " + host + ":" + port);
        }
    }
    
    public static void main(String[] args) {
        // Run a peer with a port specified via arguments
        int port = (args.length > 0) ? Integer.parseInt(args[0]) : 5000;
        new Peer(port).start();
    }
}
```

**Key Points:**
- **Dual Role:** Each peer acts as both a server (listening for incoming messages) and a client (sending messages to other peers).
- **Peer Discovery:** In real-world applications, additional logic is needed for discovering peers (e.g., a central tracker or multicast announcements).
- **State Coordination:** Requires a consistent protocol so that all peers can apply updates in a consistent manner.

---

## 4. Best Practices for Both Approaches

### 4.1. General Network Programming
- **Error Handling:** Always catch exceptions and handle network failures gracefully.
- **Resource Management:** Ensure sockets and streams are properly closed after use.
- **Threading:** Use threading or asynchronous I/O to avoid blocking the UI or other parts of your application.
- **Security:** Consider encryption (e.g., SSL/TLS) and authentication to secure your connections.

### 4.2. Client-Server Specific
- **Central Authority:** Maintain a clear separation of responsibilities where the server is the source of truth.
- **Scalability:** Design the server to handle multiple clients concurrently.
- **Synchronization:** Implement proper mechanisms (e.g., locking or atomic operations) to avoid state conflicts.

### 4.3. P2P Specific
- **Peer Discovery:** Implement robust mechanisms for peers to locate one another.
- **Decentralization:** Ensure that the loss of a single node doesn’t disrupt the entire network.
- **Data Consistency:** Use consensus protocols or conflict resolution techniques to maintain a consistent state across peers.

---

## 5. Conclusion

Java network programming offers a variety of ways to coordinate applications that need to share the same view of data. In a client-server model, the server acts as the centralized coordinator, while in a peer-to-peer setup, each node collaborates to maintain a synchronized state. Both approaches have their own challenges and benefits:

- **Client-Server:** Easier to manage and secure but can become a bottleneck.
- **P2P:** More resilient and scalable but requires more complex logic for discovery and state management.
