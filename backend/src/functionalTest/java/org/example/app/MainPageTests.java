package org.example.app;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainPageTests {
    private static Playwright playwright;
    private static Browser browser;
    private Page page;

    @BeforeAll
    static void beforeAll() {
        playwright = Playwright.create();
        browser = playwright.firefox().launch(
                 new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(500)
        );
    }

    @AfterAll
    static void afterAll() {
        browser.close();
        playwright.close();
    }

    @Test
    public void testHitByButton() {
        page.navigate("http://localhost:4200/home");
        int rowsBefore = page.locator("table tbody tr").count();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("0").setExact(true)).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("-")).fill("0");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("1")).nth(4).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Бахнуть орешником")).click();

        assertThat(page.locator("#boom-gif")).isVisible();
        Locator newRow = page.locator("table tbody tr").filter(new Locator.FilterOptions()
                .setHasText("0")
                .setHasText("0")
                .setHasText("1")
                .setHasText("Гойд")
        ).first();
        int rowsAfter = page.locator("table tbody tr").count();
        assertThat(newRow).isVisible();
        assertEquals(rowsBefore + 1,  rowsAfter);
    }

    @BeforeEach
    void createUser() {
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/auth/sign-up"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"username\":\"testUser\",\"password\":\"test\"}"))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode(), "Can't create test user: " + response.statusCode() + " "
                    + response.body());

            String token = extractToken(response.body());
            BrowserContext context = browser.newContext();
            context.addInitScript("localStorage.setItem('token', '" + token + "');");
            page = context.newPage();
            page.navigate("http://localhost:4200/home");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Can't create test user", e);
        }
    }

    @AfterEach
    void deleteUser() {
        Utils.deleteTestUser();
    }

    private String extractToken(String responseBody) {
        Matcher tokenMatcher = Pattern.compile("\"token\":\"([^\"]+)\"").matcher(responseBody);
        if (tokenMatcher.find()) {
            return tokenMatcher.group(1);
        }
        throw new RuntimeException("JWT token not found: " + responseBody);
    }
}
