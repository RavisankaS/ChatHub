package chatclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerConnection {

    private final String serverAddress;
    private final int serverPort;

    private Socket socket;
    private BufferedReader serverReader;
    private PrintWriter writer;

    public ServerConnection(
            String serverAddress,
            int serverPort) {

        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    // connecting to chat server & prepare input/output streams
    public void connect() throws IOException {

        socket = new Socket(
                serverAddress,
                serverPort
        );

        serverReader = new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream()
                )
        );

        writer = new PrintWriter(
                socket.getOutputStream(),
                true
        );
    }

    // sending messages or commands to server
    public void send(String message) {

        if (writer != null) {
            writer.println(message);
        }
    }

    // wait for & read the next message
    public String receive() throws IOException {

        if (serverReader != null) {
            return serverReader.readLine();
        }

        return null;
    }

    // close the connection
    public void close() {

        try {

            if (socket != null) {
                socket.close();
            }

        } catch (IOException e) {

            System.out.println(
                    "Error closing connection: "
                    + e.getMessage()
            );
        }
    }
    
    // checking client is still connected
    public boolean isConnected() {

        return socket != null
                && socket.isConnected()
                && !socket.isClosed();
    }
}