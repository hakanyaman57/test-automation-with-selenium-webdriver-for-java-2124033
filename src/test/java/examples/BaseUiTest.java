package examples;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

abstract class BaseUiTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    void setUpDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, defaultWaitTimeout());
    }

    @AfterEach
    void tearDownDriver() {
        if (driver != null && shouldQuitDriverAfterEach()) {
            driver.quit();
        }
    }

    protected Duration defaultWaitTimeout() {
        return Duration.ofSeconds(10);
    }

    protected boolean shouldQuitDriverAfterEach() {
        return true;
    }
}
