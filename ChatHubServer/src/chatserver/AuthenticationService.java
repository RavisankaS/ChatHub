package chatserver;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationService {

	// store userNames and passwords for login
    private static final Map<String, String> users =
            new HashMap<>();

    // add few sample accounts for testing the system
    static {

        users.put("ravi", "ravi1");
        users.put("john", "john2");
        users.put("sam", "sam3");
    }

    // checking whether userNames and passwords are correct
    public static boolean authenticate(
            String username,
            String password) {

    	// reject login if values are missing
        if (username == null ||
                password == null) {

            return false;
        }

        String storedPassword =
                users.get(username);

        return storedPassword != null &&
                storedPassword.equals(password);
    }
}