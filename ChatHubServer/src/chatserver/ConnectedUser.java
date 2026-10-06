package chatserver;

import java.io.PrintWriter;

public class ConnectedUser {

    private final String username;
    private final PrintWriter output;
    private final SessionContext session;

    public ConnectedUser(
            String username,
            PrintWriter output) {

        this.username = username;
        this.output = output;

        // create session for connected user
        this.session =
                new SessionContext(
                        username
                );
    }

    public String getUsername() {

        return username;
    }

    public PrintWriter getOutput() {

        return output;
    }

    public SessionContext getSession() {

        return session;
    }

    // update user's last activity time
    public void updateActivity() {

        session.updateActivity();
    }

    // send message to user's client
    public void send(
            String message) {

        output.println(message);

        // sending message also counts as activity
        updateActivity();
    }

    // mark user's session as closed
    public void closeSession() {

        session.closeSession();
    }
}