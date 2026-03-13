package examples;

import java.nio.file.Path;
import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Real-life hybrid scenario:
 * - Selenium opens the web page and clicks "Browse"
 * - SikuliX drives the native file chooser window
 *
 * This test is disabled by default because it needs an interactive desktop session
 * and template images captured from your own OS theme.
 */
class SikuliXNativeFileUploadTest {

    private WebDriver driver;

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
   // @Disabled("Manual demo: requires desktop session + image templates in src/test/resources/images")
    void uploadFileUsingNativeDialogWithSikuliX() throws Exception {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);

        driver.get("https://the-internet.herokuapp.com/upload");

        WebElement fileInput = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.id("file-upload")));
        fileInput.click(); // opens native OS dialog

        Screen screen = new Screen();

        Pattern fileNameInput = new Pattern("src/test/resources/images/file_name_input.png");
        Pattern openButton = new Pattern("src/test/resources/images/open_button.png");

        Path fileToUpload = Path.of("README.md").toAbsolutePath();

        screen.wait(fileNameInput, 10);
        screen.click(fileNameInput);
        screen.type(fileToUpload.toString());
        screen.click(openButton);

        driver.findElement(By.id("file-submit")).click();

        String uploaded = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("uploaded-files")))
                .getText();

        assertTrue(uploaded.contains("README.md"));
    }
}
