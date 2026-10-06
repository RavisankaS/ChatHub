package chatserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserApiService {

	// store users managed by REST API
    private static final Map<String, ApiUser> users =
            new ConcurrentHashMap<>();

    // add few users to test the API
    static {

        users.put(
                "ravi",
                new ApiUser(
                        "ravi",
                        "ravi@gmail.com",
                        "admin"
                )
        );

        users.put(
                "john",
                new ApiUser(
                        "john",
                        "john@gmail.com",
                        "User"
                )
        );

        users.put(
                "sam",
                new ApiUser(
                        "sam",
                        "sam@gmail.com",
                        "User"
                )
        );
    }

    // create new user if the userNmae is available
    public static synchronized boolean createUser(
            String username,
            String email,
            String role) {

        if (username == null
                || username.isBlank()) {

            return false;
        }

        // don't allow two users with the same userName
        if (users.containsKey(username)) {

            return false;
        }

        users.put(
                username,
                new ApiUser(
                        username,
                        email,
                        role
                )
        );

        return true;
    }

    // list users in alphabetical order
    public static List<ApiUser> getAllUsers() {

        List<ApiUser> userList =
                new ArrayList<>(
                        users.values()
                );

        Collections.sort(
                userList,
                (user1, user2) ->
                        user1.getUsername()
                                .compareToIgnoreCase(
                                        user2.getUsername()
                                )
        );

        return userList;
    }

    // get user user their userName
    public static ApiUser getUser(
            String username) {

        return users.get(username);
    }

    // update email and role of an existing user
    public static synchronized boolean updateUser(
            String username,
            String email,
            String role) {

        ApiUser user =
                users.get(username);

        if (user == null) {

            return false;
        }

        user.setEmail(email);
        user.setRole(role);

        return true;
    }

    // remove user from API user list
    public static synchronized boolean deleteUser(
            String username) {

        return users.remove(
                username
        ) != null;
    }

    // hold basic details of one API user
    public static class ApiUser {

        private final String username;

        private String email;

        private String role;

        public ApiUser(
                String username,
                String email,
                String role) {

            this.username = username;
            this.email = email;
            this.role = role;
        }

        public String getUsername() {

            return username;
        }

        public String getEmail() {

            return email;
        }

        public String getRole() {

            return role;
        }

        public void setEmail(
                String email) {

            this.email = email;
        }

        public void setRole(
                String role) {

            this.role = role;
        }
    }
}