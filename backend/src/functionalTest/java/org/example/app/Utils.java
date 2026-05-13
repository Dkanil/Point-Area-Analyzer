package org.example.app;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Utils {
    protected static void deleteTestUser() {
        deleteTestUser("test");
    }

    protected static void deleteTestUser(String username) {
        String url = getConfigValue("SPRING_DATASOURCE_URL", "spring.datasource.url", "jdbc:postgresql://localhost:5433/studs");
        String dbUsername = getConfigValue("SPRING_DATASOURCE_USERNAME", "admin");
        String dbPassword = getConfigValue("SPRING_DATASOURCE_PASSWORD", "admin");

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE username = ?")) {
            statement.setString(1, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Can't delete test user. " + e.getMessage());
        }
    }

    private static String getConfigValue(String envName, String defaultValue) {
        return getConfigValue(envName, envName, defaultValue);
    }

    private static String getConfigValue(String envName, String propertyName, String defaultValue) {
        String value = System.getenv(envName);
        if (value != null && !value.isBlank()) return value;
        value = System.getProperty(propertyName);
        if (value != null && !value.isBlank()) return value;
        return defaultValue;
    }

    protected static String frontendUrl(String path) {
        return buildUrl(getConfigValue("FRONTEND_BASE_URL", "frontend.baseUrl", "http://localhost:4200"), path);
    }

    protected static String backendUrl(String path) {
        return buildUrl(getConfigValue("BACKEND_BASE_URL", "backend.baseUrl", "http://localhost:8080"), path);
    }

    private static String buildUrl(String baseUrl, String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return baseUrl.endsWith("/") ? baseUrl + path : baseUrl + "/" + path;
    }

    protected static void createTestUser() {
        createTestUser("test");
    }

    protected static String createTestUser(String username) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(URI.create(backendUrl("/auth/sign-up")))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, "test")))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode(), "Can't create test user: " + response.statusCode() + " "
                    + response.body());
            return extractToken(response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Can't create test user", e);
        }
    }

    private static String extractToken(String responseBody) {
        Matcher tokenMatcher = Pattern.compile("\"token\":\"([^\"]+)\"").matcher(responseBody);
        if (tokenMatcher.find()) {
            return tokenMatcher.group(1);
        }
        throw new RuntimeException("JWT token not found: " + responseBody);
    }
}
