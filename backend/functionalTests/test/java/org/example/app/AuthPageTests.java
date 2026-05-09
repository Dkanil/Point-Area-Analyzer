package org.example.app;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthPageTests {
    private static Playwright playwright;
    private static Browser browser;

    @BeforeAll
    static void initPlaywright() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
    }

    @AfterAll
    static void closePlaywright() {
        browser.close();
        playwright.close();
    }

    @Test
    public void testLoginSuccess() {
        try (Page page = browser.newPage()) {
            page.navigate("http://localhost:4200/auth");
            page.locator("input[name='username']").fill("aaa");
            page.locator("input[name='password']").fill("aaa");
            page.locator(".form-buttons button").click();
            page.waitForURL("**/home");
            assertEquals("http://localhost:4200/home", page.url());
        }

    }
}