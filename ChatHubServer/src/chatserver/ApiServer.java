package chatserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ApiServer extends Thread {

    private static final int API_PORT = 8080;

    private HttpServer server;

    @Override
    public void run() {

        try {
        	// starting HTTP server on port -> 8080
            server = HttpServer.create(
                    new InetSocketAddress(API_PORT),
                    0
            );

            // register API EndPoints
            server.createContext(
                    "/api/status",
                    this::handleStatus
            );

            server.createContext(
                    "/api/auth/login",
                    this::handleLogin
            );

            server.createContext(
                    "/api/users",
                    this::handleUsers
            );

            server.setExecutor(null);

            System.out.println(
                    "HTTP API server running on port "
                    + API_PORT
            );

            server.start();

        } catch (IOException e) {

            System.out.println(
                    "HTTP API server error: "
                    + e.getMessage()
            );
        }
    }

    // return current status & API status
    private void handleStatus(
            HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod().equals("GET")) {

            sendResponse(
                    exchange,
                    405,
                    "{\"error\":\"Method Not Allowed\"}"
            );

            return;
        }

        String response =
                "{"
                + "\"application\":\"ChatHub\","
                + "\"status\":\"running\","
                + "\"apiVersion\":\"1.0\","
                + "\"tcpPort\":5000,"
                + "\"fileTransferPort\":5001,"
                + "\"httpPort\":8080"
                + "}";

        sendResponse(
                exchange,
                200,
                response
        );
    }

    // handling login requests sent through HTTP API
    private void handleLogin(
            HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod().equals("POST")) {

            sendResponse(
                    exchange,
                    405,
                    "{\"error\":\"Method Not Allowed\"}"
            );

            return;
        }
        
        // read JSON data
        String requestBody =
                new String(
                        exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8
                );

        String username =
                extractJsonValue(
                        requestBody,
                        "username"
                );

        String password =
                extractJsonValue(
                        requestBody,
                        "password"
                );

        if (username == null
                || password == null
                || username.isBlank()
                || password.isBlank()) {

            sendResponse(
                    exchange,
                    400,
                    "{\"error\":\"Username and password are required\"}"
            );

            return;
        }
        
        // checking the login info using authentication service
        boolean authenticated =
                AuthenticationService.authenticate(
                        username,
                        password
                );

        if (!authenticated) {

            AuditLogger.log(
                    "API_LOGIN_FAILED",
                    username,
                    "Invalid username or password"
            );

            sendResponse(
                    exchange,
                    401,
                    "{\"error\":\"Invalid username or password\"}"
            );

            return;
        }

        AuditLogger.log(
                "API_LOGIN",
                username,
                "HTTP API login successful"
        );

        String response =
                "{"
                + "\"success\":true,"
                + "\"username\":\""
                + escapeJson(username)
                + "\""
                + "}";

        sendResponse(
                exchange,
                200,
                response
        );
    }

    // handle all API requests
    private void handleUsers(
            HttpExchange exchange)
            throws IOException {

        String method =
                exchange.getRequestMethod();

        String path =
                exchange.getRequestURI()
                        .getPath();

        String basePath =
                "/api/users";

        // get the full list of users
        if (method.equals("GET")
                && path.equals(basePath)) {

            getAllUsers(exchange);

            return;
        }

        // create new user
        if (method.equals("POST")
                && path.equals(basePath)) {

            createUser(exchange);

            return;
        }

       // handle request for specific userName
        if (path.startsWith(
                basePath + "/"
        )) {

            String username =
                    path.substring(
                            (basePath + "/").length()
                    );

            if (username.isBlank()) {

                sendResponse(
                        exchange,
                        400,
                        "{\"error\":\"Username is required\"}"
                );

                return;
            }

            username =
                    decodePathValue(
                            username
                    );

            // get user
            if (method.equals("GET")) {

                getUser(
                        exchange,
                        username
                );

                return;
            }

            // update user
            if (method.equals("PUT")) {

                updateUser(
                        exchange,
                        username
                );

                return;
            }

            // delete user
            if (method.equals("DELETE")) {

                deleteUser(
                        exchange,
                        username
                );

                return;
            }
        }

        sendResponse(
                exchange,
                404,
                "{\"error\":\"API endpoint not found\"}"
        );
    }

  // creating new users through API
    private void createUser(
            HttpExchange exchange)
            throws IOException {

        String requestBody =
                new String(
                        exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8
                );

        String username =
                extractJsonValue(
                        requestBody,
                        "username"
                );

        String email =
                extractJsonValue(
                        requestBody,
                        "email"
                );

        String role =
                extractJsonValue(
                        requestBody,
                        "role"
                );

        if (username == null
                || username.isBlank()) {

            sendResponse(
                    exchange,
                    400,
                    "{\"error\":\"Username is required\"}"
            );

            return;
        }

        if (email == null
                || email.isBlank()) {

            sendResponse(
                    exchange,
                    400,
                    "{\"error\":\"Email is required\"}"
            );

            return;
        }
        
        // use user as the default role
        if (role == null
                || role.isBlank()) {

            role = "User";
        }

        boolean created =
                UserApiService.createUser(
                        username,
                        email,
                        role
                );

        if (!created) {

            AuditLogger.log(
                    "API_CREATE_USER_FAILED",
                    username,
                    "Username already exists"
            );

            sendResponse(
                    exchange,
                    409,
                    "{\"error\":\"Username already exists\"}"
            );

            return;
        }

        AuditLogger.log(
                "API_CREATE_USER",
                username,
                "User created through HTTP API"
        );

        String response =
                "{"
                + "\"success\":true,"
                + "\"message\":\"User created successfully\","
                + "\"username\":\""
                + escapeJson(username)
                + "\""
                + "}";

        sendResponse(
                exchange,
                201,
                response
        );
    }

   // return all users stored by API
    private void getAllUsers(
            HttpExchange exchange)
            throws IOException {

        List<UserApiService.ApiUser> users =
                UserApiService.getAllUsers();

        StringBuilder response =
                new StringBuilder();

        response.append(
                "{\"users\":["
        );

        for (int i = 0;
                i < users.size();
                i++) {

            UserApiService.ApiUser user =
                    users.get(i);

            response.append(
                    userToJson(user)
            );

            if (i < users.size() - 1) {

                response.append(",");
            }
        }

        response.append(
                "]}"
        );

        sendResponse(
                exchange,
                200,
                response.toString()
        );
    }

  // find and return single user
    private void getUser(
            HttpExchange exchange,
            String username)
            throws IOException {

        UserApiService.ApiUser user =
                UserApiService.getUser(
                        username
                );

        if (user == null) {

            sendResponse(
                    exchange,
                    404,
                    "{\"error\":\"User not found\"}"
            );

            return;
        }

        sendResponse(
                exchange,
                200,
                userToJson(user)
        );
    }

   // update email/role of an existing user
    private void updateUser(
            HttpExchange exchange,
            String username)
            throws IOException {

        UserApiService.ApiUser existingUser =
                UserApiService.getUser(
                        username
                );

        if (existingUser == null) {

            sendResponse(
                    exchange,
                    404,
                    "{\"error\":\"User not found\"}"
            );

            return;
        }

        String requestBody =
                new String(
                        exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8
                );

        String email =
                extractJsonValue(
                        requestBody,
                        "email"
                );

        String role =
                extractJsonValue(
                        requestBody,
                        "role"
                );
        // keep the old value it it was not included in the request
        if (email == null
                || email.isBlank()) {

            email =
                    existingUser.getEmail();
        }

        if (role == null
                || role.isBlank()) {

            role =
                    existingUser.getRole();
        }

        boolean updated =
                UserApiService.updateUser(
                        username,
                        email,
                        role
                );

        if (!updated) {

            sendResponse(
                    exchange,
                    404,
                    "{\"error\":\"User not found\"}"
            );

            return;
        }

        AuditLogger.log(
                "API_UPDATE_USER",
                username,
                "User updated through HTTP API"
        );

        String response =
                "{"
                + "\"success\":true,"
                + "\"message\":\"User updated successfully\","
                + "\"user\":"
                + userToJson(
                        UserApiService.getUser(
                                username
                        )
                )
                + "}";

        sendResponse(
                exchange,
                200,
                response
        );
    }

   // delete an existing user
    private void deleteUser(
            HttpExchange exchange,
            String username)
            throws IOException {

        UserApiService.ApiUser existingUser =
                UserApiService.getUser(
                        username
                );

        if (existingUser == null) {

            sendResponse(
                    exchange,
                    404,
                    "{\"error\":\"User not found\"}"
            );

            return;
        }

        boolean deleted =
                UserApiService.deleteUser(
                        username
                );

        if (!deleted) {

            sendResponse(
                    exchange,
                    404,
                    "{\"error\":\"User not found\"}"
            );

            return;
        }

        AuditLogger.log(
                "API_DELETE_USER",
                username,
                "User deleted through HTTP API"
        );

        String response =
                "{"
                + "\"success\":true,"
                + "\"message\":\"User deleted successfully\","
                + "\"username\":\""
                + escapeJson(username)
                + "\""
                + "}";

        sendResponse(
                exchange,
                200,
                response
        );
    }

   // convert user object into JSON format
    private String userToJson(
            UserApiService.ApiUser user) {

        return "{"
                + "\"username\":\""
                + escapeJson(
                        user.getUsername()
                )
                + "\","
                + "\"email\":\""
                + escapeJson(
                        user.getEmail()
                )
                + "\","
                + "\"role\":\""
                + escapeJson(
                        user.getRole()
                )
                + "\""
                + "}";
    }

   // get a simple string value from JSON request
    private String extractJsonValue(
            String json,
            String key) {

        String searchKey =
                "\"" + key + "\"";

        int keyPosition =
                json.indexOf(searchKey);

        if (keyPosition == -1) {

            return null;
        }

        int colonPosition =
                json.indexOf(
                        ":",
                        keyPosition
                );

        if (colonPosition == -1) {

            return null;
        }

        int firstQuote =
                json.indexOf(
                        "\"",
                        colonPosition + 1
                );

        if (firstQuote == -1) {

            return null;
        }

        int secondQuote =
                json.indexOf(
                        "\"",
                        firstQuote + 1
                );

        if (secondQuote == -1) {

            return null;
        }

        return json.substring(
                firstQuote + 1,
                secondQuote
        );
    }

   // escape characters that could cause issues in JSON
    private String escapeJson(
            String value) {

        if (value == null) {

            return "";
        }

        return value
                .replace(
                        "\\",
                        "\\\\"
                )
                .replace(
                        "\"",
                        "\\\""
                );
    }

 // handle spaces in userNames in the URL paths
    private String decodePathValue(
            String value) {

        return value.replace(
                "%20",
                " "
        );
    }

   // send final HTTP response
    private void sendResponse(
            HttpExchange exchange,
            int statusCode,
            String response)
            throws IOException {

        byte[] responseBytes =
                response.getBytes(
                        StandardCharsets.UTF_8
                );

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin",
                "*"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers",
                "Content-Type"
        );

        exchange.sendResponseHeaders(
                statusCode,
                responseBytes.length
        );

        try (
                OutputStream output =
                        exchange.getResponseBody()
        ) {

            output.write(
                    responseBytes
            );
        }
    }
}