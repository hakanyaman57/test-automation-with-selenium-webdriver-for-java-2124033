package examples;

import java.nio.file.Path;
import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

class SeleniumOnlyWebPageTest {

    private static final Duration BEFORE_CLICK_PAUSE = Duration.ofSeconds(2);
    private static final Duration DEMO_PAUSE = Duration.ofSeconds(3);
    private WebDriver driver;

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void shouldClickTheSameButtonUsingOnlySelenium() throws Exception {
        // This version uses only Selenium.
        // There is no pixel search and no need for SikuliX permissions.
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);

        // We reuse the exact same local page used by the hybrid example.
        // That makes the comparison easy during the training:
        // same page, same button, different automation approach.
        Path demoPage = Path.of("src", "test", "resources", "pages", "sikulix-web-demo.html").toAbsolutePath();
        driver.get(demoPage.toUri().toString());

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Selenium reads the button directly from the DOM by its id.
        // This is the key difference from SikuliX: Selenium interacts with HTML elements,
        // not with the visual pixels rendered on the screen.
        WebElement actionButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("sikuli-button")));

        // Pause briefly so the page is visible before Selenium performs the click.
        Thread.sleep(BEFORE_CLICK_PAUSE.toMillis());
        actionButton.click();

        // After the DOM click, we verify the status text changed.
        WebElement status = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("status")));
        assertEquals("Clicked with SikuliX", status.getText());

        // Keep the browser open briefly so the result is visible during a live demo.
        // This pause is only for presentation purposes, not for normal fast test execution.
        Thread.sleep(DEMO_PAUSE.toMillis());
    }
}
