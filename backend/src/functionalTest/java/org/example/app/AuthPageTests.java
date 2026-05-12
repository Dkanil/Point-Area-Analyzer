package org.example.app;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthPageTests {
    private static Playwright playwright;
    private static Browser browser;

    @BeforeAll
    static void initPlaywright() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(500));
    }

    @AfterAll
    static void closePlaywright() {
        browser.close();
        playwright.close();
    }

    @Test
    public void testLoginSuccess() {
        String username = "testLogin";
        createUser(username, "test");
        try (Page page = browser.newPage()) {
            page.navigate("http://localhost:4200/auth");
            page.locator("input[name='username']").fill(username);
            page.locator("input[name='password']").fill("test");
            page.locator(".form-buttons button").click();

            page.waitForURL("**/home");
            assertEquals("http://localhost:4200/home", page.url());
        } finally {
            deleteTestUser(username);
        }
    }

    @Test
    public void testRegisterSuccess() {
        String username = "testRegister";
        try (Page page = browser.newPage()) {
            page.navigate("http://localhost:4200/auth");
            page.locator("input[name='username']").fill(username);
            page.locator("input[name='password']").fill("test");
            page.locator("input[type='checkbox']").click();
            page.locator(".form-buttons button").click();

            page.waitForURL("**/home");
            assertEquals("http://localhost:4200/home", page.url());
        } finally {
            deleteTestUser(username);
        }
    }

    @Test
    public void testRegisterExistingUser() {
        String username = "testExistingUser";
        createUser(username, "test");
        try (Page page = browser.newPage()) {
            page.navigate("http://localhost:4200/auth");
            page.locator("input[name='username']").fill(username);
            page.locator("input[name='password']").fill("test");
            page.locator("input[type='checkbox']").click();
            page.locator(".form-buttons button").click();

            page.waitForSelector(".error-message");
            assertEquals("Данное имя пользователя уже занято", page.locator(".error-message").innerText());
        } finally {
            deleteTestUser(username);
        }
    }


    private void createUser(String username, String password) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/auth/sign-up"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password)))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertTrue(response.statusCode() >= 200 && response.statusCode() < 300,
                    "Не удалось создать пользователя для теста: " + response.statusCode() + " " + response.body());
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось создать пользователя для теста", e);
        }
    }

    private void deleteTestUser(String username) {
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

    private String getConfigValue(String envName, String defaultValue) {
        String value = System.getenv(envName);
        if (value != null && !value.isBlank()) return value;
        value = System.getProperty(envName);
        if (value != null && !value.isBlank()) return value;
        return defaultValue;
    }
}
