package examples;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.time.Duration;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
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
 * This test is disabled by default because it needs an interactive desktop
 * session
 * and template images captured from your own OS theme.
 */
class SikuliXNativeFileUploadTest {

    private WebDriver driver;

    @AfterEach
    void tearDown() {
        if (driver != null) {
            // driver.quit();
        }
    }

    @Test
    void uploadFileUsingNativeDialogWithSikuliX() throws Exception {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);

        driver.get("https://the-internet.herokuapp.com/upload");

        // Selenium still locates the file input in the DOM.
        // We do not click it with Selenium because some driver/browser combinations
        // reject click() on input[type=file]. Instead, we turn its rendered pixels
        // into a SikuliX pattern and click it visually on screen.
        WebElement fileInput = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.id("file-upload")));

        Screen screen = new Screen();
        BufferedImage chooseFileImage = ImageIO.read(new ByteArrayInputStream(fileInput.getScreenshotAs(OutputType.BYTES)));
        Pattern chooseFileButton = new Pattern(chooseFileImage).similar(0.95f);

        screen.wait(chooseFileButton, 10);
        screen.click(chooseFileButton); // opens native OS dialog visually

        // These two images must come from the native Windows/macOS file chooser dialog,
        // not from the browser page itself.
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
