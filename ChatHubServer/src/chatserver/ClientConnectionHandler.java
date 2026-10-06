package chatserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnectionHandler extends Thread {

    private final Socket clientSocket;

    public ClientConnectionHandler(
            Socket clientSocket) {

        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {

        String username = null;

        boolean sessionRegistered = false;

        ConnectedUser connectedUser = null;

        try {

        	// set up streams for communication with the client
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    clientSocket.getInputStream()
                            )
                    );

            PrintWriter writer =
                    new PrintWriter(
                            clientSocket.getOutputStream(),
                            true
                    );

            // first message should contain login info
            String loginRequest =
                    reader.readLine();

            if (loginRequest == null) {

                clientSocket.close();

                return;
            }

            System.out.println(
                    "Received: "
                    + loginRequest
            );

            // making sure client sends login request first
            if (!loginRequest.startsWith(
                    "LOGIN|"
            )) {

                writer.println(
                        "ERROR|Please login first"
                );

                AuditLogger.log(
                        "INVALID_REQUEST",
                        "UNKNOWN",
                        "Client attempted to connect without LOGIN"
                );

                clientSocket.close();

                return;
            }

            // split login request into userName & password
            String[] loginData =
                    loginRequest.split(
                            "\\|",
                            3
                    );

            if (loginData.length != 3) {

                writer.println(
                        "LOGIN_FAIL|Invalid login format"
                );

                AuditLogger.log(
                        "LOGIN_FAILED",
                        "UNKNOWN",
                        "Invalid login format"
                );

                clientSocket.close();

                return;
            }

            username =
                    loginData[1];

            String password =
                    loginData[2];

            // checking login info
            boolean authenticated =
                    AuthenticationService.authenticate(
                            username,
                            password
                    );

            if (!authenticated) {

                writer.println(
                        "LOGIN_FAIL|Invalid username or password"
                );

                AuditLogger.log(
                        "LOGIN_FAILED",
                        username,
                        "Invalid username or password"
                );

                clientSocket.close();

                return;
            }

            // avoid same account from logging in twice
            if (SessionRegistry.isOnline(
                    username
            )) {

                writer.println(
                        "LOGIN_FAIL|User is already online"
                );

                AuditLogger.log(
                        "LOGIN_FAILED",
                        username,
                        "User is already online"
                );

                clientSocket.close();

                return;
            }

            // create session for successfully logged-in user
            connectedUser =
                    new ConnectedUser(
                            username,
                            writer
                    );

            boolean registered =
                    SessionRegistry.register(
                            connectedUser
                    );

            if (!registered) {

                writer.println(
                        "LOGIN_FAIL|Unable to create session"
                );

                AuditLogger.log(
                        "LOGIN_FAILED",
                        username,
                        "Unable to create session"
                );

                clientSocket.close();

                return;
            }

            sessionRegistered = true;

            // tell client that login was successful
            writer.println(
                    "LOGIN_OK|"
                    + username
            );

            System.out.println(
                    "User logged in: "
                    + username
            );

            System.out.println(
                    "Session created at: "
                    + connectedUser
                            .getSession()
                            .getLoginTimeFormatted()
            );

            AuditLogger.log(
                    "LOGIN",
                    username,
                    "Session created at "
                    + connectedUser
                            .getSession()
                            .getLoginTimeFormatted()
            );

            // update all connected client with the new user list
            SessionRegistry.broadcastUserList();

            String message;

            // keep listening for commands
            while (
                    (message = reader.readLine())
                    != null
            ) {

                connectedUser.updateActivity();

                System.out.println(
                        username
                        + " sent command: "
                        + message
                );

                // handle a message sent to another user
                if (message.startsWith(
                        "SEND|"
                )) {

                    String[] parts =
                            message.split(
                                    "\\|",
                                    3
                            );

                    if (parts.length != 3) {

                        writer.println(
                                "ERROR|Invalid SEND command"
                        );

                        AuditLogger.log(
                                "INVALID_COMMAND",
                                username,
                                "Invalid SEND command"
                        );

                        continue;
                    }

                    String recipient =
                            parts[1];

                    String text =
                            parts[2];

                    if (recipient.trim().isEmpty()) {

                        writer.println(
                                "ERROR|Recipient cannot be empty"
                        );

                        continue;
                    }

                    if (text.trim().isEmpty()) {

                        writer.println(
                                "ERROR|Message cannot be empty"
                        );

                        continue;
                    }

                    // send message through message router
                    boolean delivered =
                            MessageRouter.sendMessage(
                                    username,
                                    recipient,
                                    text
                            );

                    if (delivered) {

                        writer.println(
                                "DELIVERED|"
                                + recipient
                        );

                        AuditLogger.log(
                                "MESSAGE",
                                username,
                                "Message sent to "
                                + recipient
                        );

                    } else {

                        writer.println(
                                "ERROR|User is not online: "
                                + recipient
                        );

                        AuditLogger.log(
                                "MESSAGE_FAILED",
                                username,
                                "Recipient offline: "
                                + recipient
                        );
                    }
                }

                // send current online user list
                else if (
                        message.equals(
                                "USERS"
                        )
                ) {

                    writer.println(
                            "USERS|"
                            + SessionRegistry.getOnlineUsers()
                    );

                    AuditLogger.log(
                            "USER_LIST",
                            username,
                            "Requested online user list"
                    );
                }

                // send current session info
                else if (
                        message.equals(
                                "SESSION"
                        )
                ) {

                    writer.println(
                            "SESSION|"
                            + connectedUser
                                    .getSession()
                                    .getLoginTimeFormatted()
                            + "|"
                            + connectedUser
                                    .getSession()
                                    .getLastActivityFormatted()
                            + "|"
                            + connectedUser
                                    .getSession()
                                    .isActive()
                    );
                }

                // handle a user logout
                else if (
                        message.equals(
                                "LOGOUT"
                        )
                ) {

                    writer.println(
                            "LOGOUT_OK"
                    );

                    AuditLogger.log(
                            "LOGOUT",
                            username,
                            "User logged out"
                    );

                    connectedUser.closeSession();

                    break;
                }

                // tell client if the command is invalid
                else {

                    writer.println(
                            "ERROR|Unknown command"
                    );

                    AuditLogger.log(
                            "INVALID_COMMAND",
                            username,
                            "Unknown command: "
                            + message
                    );
                }
            }

        } catch (IOException e) {

            System.out.println(
                    "Client error: "
                    + e.getMessage()
            );

            if (username != null) {

                AuditLogger.log(
                        "CONNECTION_ERROR",
                        username,
                        e.getMessage()
                );
            }

        } finally {

        	// remove user from the online list
            if (
                    sessionRegistered
                    && username != null
            ) {

                if (connectedUser != null) {

                    connectedUser.closeSession();
                }

                SessionRegistry.remove(
                        username
                );

                System.out.println(
                        "User disconnected: "
                        + username
                );

                System.out.println(
                        "Online users: "
                        + SessionRegistry.getOnlineUsers()
                );

                AuditLogger.log(
                        "DISCONNECT",
                        username,
                        "Client connection closed"
                );
            }

            try {

                clientSocket.close();

            } catch (IOException e) {

                System.out.println(
                        "Error closing client connection."
                );
            }
        }
    }
}