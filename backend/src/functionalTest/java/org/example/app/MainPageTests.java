package org.example.app;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainPageTests {
    private static Playwright playwright;
    private static Browser browser;
    private Page page;

    @BeforeAll
    static void beforeAll() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                //new BrowserType.LaunchOptions().setHeadless(false)
        );
    }

    @AfterAll
    static void afterAll() {
        browser.close();
        playwright.close();
    }


    @BeforeEach
    void beforeEach() {
        createUser("test");
    }

    void createUser(String username) {
        String token = Utils.createTestUser(username);
        BrowserContext context = browser.newContext();
        context.addInitScript("localStorage.setItem('token', '" + token + "');");
        page = context.newPage();
        page.navigate(Utils.frontendUrl("/home"));
    }

    @AfterEach
    void afterEach() {
        if (page != null) {
            page.close();
        }
        Utils.deleteTestUser();
    }

    @Test
    public void testHitByButton() {
        int rowsBefore = page.locator("table tbody tr").count();
        submitPoint("0", "0");

        assertThat(page.locator("#boom-gif")).isVisible();
        Locator newRow = page.locator("table tbody tr").filter(new Locator.FilterOptions()
                .setHasText("0")
                .setHasText("0")
                .setHasText("1")
                .setHasText("Гойд")
        ).first();
        int rowsAfter = page.locator("table tbody tr").count();
        assertThat(newRow).isVisible();
        assertEquals(rowsBefore + 1, rowsAfter);
    }

    @Test
    public void testHitByClick() {
        int rowsBefore = page.locator("table tbody tr").count();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("2")).nth(2).click();
        page.locator("#graphCanvas").click(new Locator.ClickOptions().setPosition(150, 150));
        assertThat(page.locator("#boom-gif")).isVisible();
        Locator newRow = page.locator("table tbody tr").filter(new Locator.FilterOptions()
                .setHasText("0")
                .setHasText("0")
                .setHasText("2")
                .setHasText("Гойд")
        ).first();
        int rowsAfter = page.locator("table tbody tr").count();
        assertThat(newRow).isVisible();
        assertEquals(rowsBefore + 1, rowsAfter);
    }

    @Test
    public void testMissByButton() {
        int rowsBefore = page.locator("table tbody tr").count();
        submitPoint("2", "4");
        Locator newRow = page.locator("table tbody tr").filter(new Locator.FilterOptions()
                .setHasText("2")
                .setHasText("4")
                .setHasText("1")
                .setHasText("Потужно")
        ).first();
        assertThat(page.locator("#miss-gif")).isVisible();
        assertThat(newRow).isVisible();
        assertEquals(rowsBefore + 1, page.locator("table tbody tr").count());
    }

    @Test
    public void testEmptyCordsSubmitFail() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Бахнуть орешником")).click();
        page.waitForSelector(".error-message");
        assertEquals("Введите координату Y", page.locator(".error-message").innerText());
    }

    @Test
    public void testInvalidCordsSubmitFail() {
        page.locator("input[name='y']").fill("AAAA");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Бахнуть орешником")).click();
        page.waitForSelector(".error-message");
        assertEquals("Координата Y должна быть числом в диапазоне -5..5", page.locator(".error-message").innerText());
    }

    @Test
    public void testTwoUsers() {
        page.locator("#graphCanvas").click(new Locator.ClickOptions().setPosition(150, 150));
        Locator user1Point = page.locator("table tbody tr").filter(new Locator.FilterOptions()
                .setHasText("0")
                .setHasText("0")
                .setHasText("2")
                .setHasText("Гойд")
        ).first();
        assertThat(user1Point).isVisible();
        if (page != null) {
            page.close();
        }
        createUser("test2");
        int user1PointForUser2 = page.locator("table tbody tr").filter(new Locator.FilterOptions()
                .setHasText("0")
                .setHasText("0")
                .setHasText("2")
                .setHasText("Гойд")
        ).count();
        assertEquals(0, user1PointForUser2);

        Utils.deleteTestUser("test2");
    }

    @Test
    public void testLogoutSuccess() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Выйти")).click();
        page.waitForURL("**/auth");
        assertEquals(Utils.frontendUrl("/auth"), page.url());
    }

    private void submitPoint(String x, String y) {
        submitPoint(x, y, "1");
    }

    private void submitPoint(String x, String y, String r) {
        page.locator(".button-row").nth(0)
                .getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName(x).setExact(true)).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("-")).fill(y);
        page.locator(".button-row").nth(1)
                .getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName(r).setExact(true)).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Бахнуть орешником")).click();
    }


    @Test
    public void testSubmitCorner() {
        int rowsBefore = page.locator("table tbody tr").count();
        submitPoint("2", "1", "2");
        Locator newRow = page.locator("table tbody tr").nth(rowsBefore);
        assertThat(newRow).isVisible();
        assertThat(newRow).containsText("2");
        assertEquals(rowsBefore + 1, page.locator("table tbody tr").count());
    }

    @Test
    public void testDataReload() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("2")).nth(2).click();
        page.locator("#graphCanvas").click(new Locator.ClickOptions().setPosition(150, 150));
        Locator row = page.locator("table tbody tr").filter(new Locator.FilterOptions()
                .setHasText("0")
                .setHasText("2")).first();
        assertThat(row).isVisible();
        page.reload();
        page.waitForSelector("table tbody tr");
        int rowsAfterReload = page.locator("table tbody tr").count();
        assertEquals(1, rowsAfterReload);
        assertThat(row).isVisible();
    }
}
