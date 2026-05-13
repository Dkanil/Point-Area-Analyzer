package org.example.app;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthPageTests {
    private static Playwright playwright;
    private static Browser browser;

    @BeforeAll
    static void initPlaywright() {
        playwright = Playwright.create();
        browser = playwright.firefox().launch(
                 new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(500)
        );
    }

    @AfterAll
    static void closePlaywright() {
        browser.close();
        playwright.close();
    }

    @Test
    public void testLoginSuccess() {
        String username = "testLogin";
        createUser(username);
        try (Page page = browser.newPage()) {
            page.navigate("http://localhost:4200/auth");
            page.locator("input[name='username']").fill(username);
            page.locator("input[name='password']").fill("test");
            page.locator(".form-buttons button").click();

            page.waitForURL("**/home");
            assertEquals("http://localhost:4200/home", page.url());
        } finally {
            Utils.deleteTestUser(username);
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
            Utils.deleteTestUser(username);
        }
    }

    @Test
    public void testRegisterExistingUser() {
        String username = "testExistingUser";
        createUser(username);
        try (Page page = browser.newPage()) {
            page.navigate("http://localhost:4200/auth");
            page.locator("input[name='username']").fill(username);
            page.locator("input[name='password']").fill("test");
            page.locator("input[type='checkbox']").click();
            page.locator(".form-buttons button").click();

            page.waitForSelector(".error-message");
            assertEquals("Данное имя пользователя уже занято", page.locator(".error-message").innerText());
        } finally {
            Utils.deleteTestUser(username);
        }
    }


    private void createUser(String username) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/auth/sign-up"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, "test")))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode(), "Can't create test user: " + response.statusCode() + " "
                    + response.body());
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Can't create test user", e);
        }
    }
}
