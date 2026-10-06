package chatserver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionRegistry {

	// keep track of users who are currently connected
    private static final Map<String, ConnectedUser> users =
            new ConcurrentHashMap<>();

    // add new user to online list
    public static boolean register(ConnectedUser user) {

        return users.putIfAbsent(
                user.getUsername(),
                user
        ) == null;
    }

    // remove user when their connection ends
    public static void remove(String username) {

        users.remove(username);

        // let the remaining user know about the change
        broadcastUserList();
    }

    // find connected user by their userName
    public static ConnectedUser find(String username) {

        return users.get(username);
    }

    // check whether a user is online
    public static boolean isOnline(String username) {

        return users.containsKey(username);
    }

    // get all currently online userNames
    public static String getOnlineUsers() {

        if (users.isEmpty()) {

            return "No users online";
        }

        return String.join(
                ", ",
                users.keySet()
        );
    }

    // sending updated online user list to all connected clients
    public static void broadcastUserList() {

        String userList =
                getOnlineUsers();

        for (ConnectedUser user : users.values()) {

            user.send(
                    "USERS|"
                    + userList
            );
        }
    }
}