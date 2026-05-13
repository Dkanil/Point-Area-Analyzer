package org.example.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Utils {
    protected static void deleteTestUser() {
        deleteTestUser("test");
    }

    protected static void deleteTestUser(String username) {
        String url = getConfigValue("SPRING_DATASOURCE_URL", "jdbc:postgresql://localhost:5433/studs");
        String dbUsername = getConfigValue("SPRING_DATASOURCE_USERNAME", "admin");
        String dbPassword = getConfigValue("SPRING_DATASOURCE_PASSWORD", "admin");

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE username = ?")) {
            statement.setString(1, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Can't delete user" + username + ". " + e.getMessage());
        }
    }
    private static String getConfigValue(String envName, String defaultValue) {
        String value = System.getenv(envName);
        if (value != null && !value.isBlank()) return value;
        value = System.getProperty(envName);
        if (value != null && !value.isBlank()) return value;
        return defaultValue;
    }
}
