package chatserver;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SessionContext {

    private final String username;
    private final LocalDateTime loginTime;

    private LocalDateTime lastActivity;
    private boolean active;

    // format for showing session times
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd HH:mm:ss"
            );

    public SessionContext(
            String username) {

        this.username = username;

        // save time when the user logs in
        this.loginTime =
                LocalDateTime.now();

        // at the beginning, login time is also the last activity time
        this.lastActivity =
                this.loginTime;

        this.active = true;
    }

    public String getUsername() {

        return username;
    }

    public LocalDateTime getLoginTime() {

        return loginTime;
    }

    public LocalDateTime getLastActivity() {

        return lastActivity;
    }

    public boolean isActive() {

        return active;
    }

    // update last activity
    public void updateActivity() {

        lastActivity =
                LocalDateTime.now();
    }

    // mark the session as inactive after user logging out
    public void closeSession() {

        active = false;

        updateActivity();
    }

    public String getLoginTimeFormatted() {

        return loginTime.format(
                TIME_FORMAT
        );
    }

    public String getLastActivityFormatted() {

        return lastActivity.format(
                TIME_FORMAT
        );
    }
}