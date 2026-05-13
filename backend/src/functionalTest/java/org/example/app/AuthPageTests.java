package org.example.app;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthPageTests {
    private static Playwright playwright;
    private static Browser browser;

    @BeforeAll
    static void initPlaywright() {
        playwright = Playwright.create();
        browser = playwright.firefox().launch();
    }

    @AfterAll
    static void closePlaywright() {
        browser.close();
        playwright.close();
    }

    @AfterEach
    void deleteTestUser() {
        Utils.deleteTestUser();
    }

    @Test
    public void testLoginSuccess() {
        Utils.createTestUser();
        try (Page page = browser.newPage()) {
            page.navigate(Utils.frontendUrl("/auth"));
            page.locator("input[name='username']").fill("test");
            page.locator("input[name='password']").fill("test");
            page.locator(".form-buttons button").click();

            page.waitForURL("**/home");
            assertEquals(Utils.frontendUrl("/home"), page.url());
        }
    }

    @Test
    public void testSkipAuthFail() {
        try (Page page = browser.newPage()) {
            page.navigate(Utils.frontendUrl("/home"));
            page.waitForURL("**/auth");
            assertEquals(Utils.frontendUrl("/auth"), page.url());
        }
    }

    @Test
    public void testLoginInvalidPasswordFail() {
        Utils.createTestUser();
        loginInvalid("test", "wrong");
    }

    @Test
    public void testLoginInvalidUsernameFail() {
        loginInvalid("test", "test");
    }

    private void loginInvalid(String username, String password) {
        try (Page page = browser.newPage()) {
            page.navigate(Utils.frontendUrl("/auth"));
            page.locator("input[name='username']").fill(username);
            page.locator("input[name='password']").fill(password);
            page.locator(".form-buttons button").click();

            page.waitForSelector(".error-message");
            assertEquals("Неверное имя пользователя или пароль", page.locator(".error-message").innerText());
        }
    }

    @Test
    public void testRegisterSuccess() {
        try (Page page = browser.newPage()) {
            page.navigate(Utils.frontendUrl("/auth"));
            page.locator("input[name='username']").fill("test");
            page.locator("input[name='password']").fill("test");
            page.locator("input[type='checkbox']").click();
            page.locator(".form-buttons button").click();

            page.waitForURL("**/home");
            assertEquals(Utils.frontendUrl("/home"), page.url());
        }
    }

    @Test
    public void testRegisterExistingUser() {
        Utils.createTestUser();
        try (Page page = browser.newPage()) {
            page.navigate(Utils.frontendUrl("/auth"));
            page.locator("input[name='username']").fill("test");
            page.locator("input[name='password']").fill("test");
            page.locator("input[type='checkbox']").click();
            page.locator(".form-buttons button").click();

            page.waitForSelector(".error-message");
            assertEquals("Данное имя пользователя уже занято", page.locator(".error-message").innerText());
        }
    }
}
