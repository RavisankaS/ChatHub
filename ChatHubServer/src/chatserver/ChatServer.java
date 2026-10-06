package chatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {

	// main TCP port using for client connection
    private static final int PORT = 5000;

    public static void main(String[] args) {

        System.out.println(
        		"Starting ChatHub server........."
        );

        // Start REST API in the background
        ApiServer apiServer =
                new ApiServer();

        apiServer.start();

        // start separate service for file transfer
        FileTransferService fileService =
                new FileTransferService();

        fileService.start();

        try (
        		// open main server socket for chat connection
                ServerSocket serverSocket =
                        new ServerSocket(PORT)
        ) {

            System.out.println(
                    "TCP server is running on port "
                    + PORT
            );

            System.out.println(
                    "Waiting for clients..."
            );

            // keep server running & waiting for new clients
            while (true) {

                Socket clientSocket =
                        serverSocket.accept();

                System.out.println(
                        "Client connected: "
                        + clientSocket.getInetAddress()
                );

                // give each client its own handler thread
                ClientConnectionHandler clientHandler =
                        new ClientConnectionHandler(
                                clientSocket
                        );

                clientHandler.start();
            }

        } catch (IOException e) {

        	// show the error if the server cannot start or stops 
            System.out.println(
                    "Server error: "
                    + e.getMessage()
            );
        }
    }
}