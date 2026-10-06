package chatclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {

        System.out.println("ChatHub CLIENT");

        try (
                Socket socket = new Socket(
                        SERVER_ADDRESS,
                        SERVER_PORT
                );

                BufferedReader serverReader =
                        new BufferedReader(
                                new InputStreamReader(
                                        socket.getInputStream()
                                )
                        );

                BufferedReader keyboardReader =
                        new BufferedReader(
                                new InputStreamReader(
                                        System.in
                                )
                        );

                PrintWriter writer =
                        new PrintWriter(
                                socket.getOutputStream(),
                                true
                        )
        ) {

            System.out.println(
                    "Connected to ChatHub server."
            );

            System.out.println();

            // Get login info
            System.out.print("Username: ");
            String username =
                    keyboardReader.readLine();

            System.out.print("Password: ");
            String password =
                    keyboardReader.readLine();

            writer.println(
                    "LOGIN|"
                    + username
                    + "|"
                    + password
            );

            String loginResponse =
                    serverReader.readLine();

            if (loginResponse == null) {

                System.out.println(
                        "Server closed the connection."
                );

                return;
            }

            // Login failure
            if (!loginResponse.startsWith(
                    "LOGIN_OK|")) {

                System.out.println();
                System.out.println(
                        "Login failed."
                );

                if (loginResponse.contains("|")) {

                    String reason =
                            loginResponse.substring(
                                    loginResponse.indexOf("|") + 1
                            );

                    System.out.println(
                            "Reason: " + reason
                    );
                }

                return;
            }


            // Login successful!
            String loggedInUser =
                    loginResponse.substring(
                            "LOGIN_OK|".length()
                    );

            System.out.println();
            System.out.println(
                    "Login successful!"
            );

            System.out.println(
                    "Welcome, " + loggedInUser + "!"
            );

            System.out.println();

            System.out.println(
                    "Available Commands:"
            );

            System.out.println(
                    "/send username message"
            );

            System.out.println(
                    "/users"
            );

            System.out.println(
                    "/exit"
            );

            System.out.println();

            // separate thread listens for messages - coming from server
            Thread receiveThread =
                    new Thread(() -> {

                        try {

                            String serverMessage;

                            while ((serverMessage =
                                    serverReader.readLine())
                                    != null) {

                                if (serverMessage.startsWith(
                                        "MESSAGE|")) {

                                    String[] parts =
                                            serverMessage.split(
                                                    "\\|",
                                                    3
                                            );

                                    if (parts.length == 3) {

                                        String sender =
                                                parts[1];

                                        String message =
                                                parts[2];

                                        System.out.println();

                                        System.out.println(
                                                "["
                                                + sender
                                                + "] "
                                                + message
                                        );

                                        System.out.print(
                                                "You: "
                                        );
                                    }

                                }

                                // Other server response
                                else {

                                    System.out.println();

                                    System.out.println(
                                            "Server: "
                                            + serverMessage
                                    );

                                    System.out.print(
                                            "You: "
                                    );
                                }
                            }

                        } catch (IOException e) {

                            System.out.println(
                                    "Disconnected from server."
                            );
                        }
                    });

            receiveThread.start();

            // read user commands

            while (true) {

                System.out.print("You: ");

                String input =
                        keyboardReader.readLine();

                if (input == null) {

                    break;
                }

                // exit
                if (input.equalsIgnoreCase(
                        "/exit")) {

                    writer.println("LOGOUT");

                    break;
                }

                // message sending
                if (input.startsWith("/send ")) {

                    String command =
                            input.substring(6);

                    String[] parts =
                            command.split(
                                    "\\s+",
                                    2
                            );

                    if (parts.length < 2) {

                        System.out.println(
                                "Usage: /send username message"
                        );

                        continue;
                    }

                    String recipient =
                            parts[0];

                    String message =
                            parts[1];

                    writer.println(
                            "SEND|"
                            + recipient
                            + "|"
                            + message
                    );

                }

                // online users
                else if (input.equalsIgnoreCase(
                        "/users")) {

                    writer.println(
                            "USERS"
                    );
                }

                // unrecognized commands
                else {

                    System.out.println(
                            "Unknown command."
                    );

                    System.out.println(
                            "Available commands: /send, /users, and /exit."
                    );
                }
            }

        } catch (IOException e) {

            System.out.println();
            System.out.println( "Couldn't connect to ChatHub server." );

            System.out.println(
                    "Details: "
                    + e.getMessage()
            );
        }
    }
}