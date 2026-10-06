package chatserver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditLogger {

    private static final String LOG_DIRECTORY =
            "logs";

    private static final String LOG_FILE =
            "ChatHub-audit.log";

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd HH:mm:ss"
            );

    // save important server event to audit log
    public static synchronized void log(
            String event,
            String username,
            String details) {

    	// create logs folder if it does not already exist
        File directory =
                new File(LOG_DIRECTORY);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        // add the current date and time to log entry
        String timestamp =
                LocalDateTime.now().format(
                        TIME_FORMAT
                );

        String logEntry =
                timestamp
                + " | "
                + event
                + " | "
                + username
                + " | "
                + details;

        // show the event in the server console
        System.out.println(
                "AUDIT: "
                + logEntry
        );

        File logFile =
                new File(
                        directory,
                        LOG_FILE
                );

        try (
        		// open log file in append mode
                FileWriter fileWriter =
                        new FileWriter(
                                logFile,
                                true
                        );

                PrintWriter writer =
                        new PrintWriter(
                                fileWriter
                        )
        ) {

            writer.println(logEntry);

        } catch (IOException e) {

            System.out.println(
                    "Unable to write audit log: "
                    + e.getMessage()
            );
        }
    }
}