package chatserver;

public class MessageRouter {

	// sends a message to selected online user
    public static boolean sendMessage(
            String sender,
            String recipient,
            String message) {

    	// find the recipient's current connection
        ConnectedUser target =
                SessionRegistry.find(recipient);

        if (target == null) {
        	
        	// user is not currently online
            return false;
        }

        // send message to recipient
        target.send(
                "MESSAGE|"
                + sender
                + "|"
                + message
        );

        return true;
    }
}