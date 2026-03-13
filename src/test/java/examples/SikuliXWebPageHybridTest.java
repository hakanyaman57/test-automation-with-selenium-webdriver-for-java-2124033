package examples;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
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

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SikuliXWebPageHybridTest {

    private WebDriver driver;

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void shouldUseSeleniumForThePageAndSikuliXForOneVisualStep() throws Exception {
        // Selenium still opens the browser and handles normal page navigation.
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);

        // We use a local HTML file so the demo does not depend on internet access.
        Path demoPage = Path.of("src", "test", "resources", "pages", "sikulix-web-demo.html").toAbsolutePath();
        driver.get(demoPage.toUri().toString());

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Step 1: Selenium proves that the page is loaded and the DOM is accessible.
        WebElement status = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("status")));
        assertEquals("Waiting for click", status.getText());

        // Step 2: Selenium finds the button in the DOM.
        // We then take an image of the real rendered button.
        // This image becomes the SikuliX search pattern.
        WebElement actionButton = driver.findElement(By.id("sikuli-button"));
        byte[] buttonBytes = actionButton.getScreenshotAs(OutputType.BYTES);
        BufferedImage buttonImage = ImageIO.read(new ByteArrayInputStream(buttonBytes));

        // Step 3: SikuliX searches the actual screen for that image and clicks it.
        // This is the "visual automation" part: SikuliX does not care about the DOM,
        // it only looks for matching pixels on the screen.
        Screen screen = new Screen();
        Pattern buttonPattern = new Pattern(buttonImage).similar(0.95f);
        screen.wait(buttonPattern, 10);
        screen.click(buttonPattern);

        // Step 4: After the visual click, we go back to Selenium to verify the result.
        // If the click worked, the page changes the status text.
        String updatedStatus = wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("status"),
                "Clicked with SikuliX")) ? driver.findElement(By.id("status")).getText() : "";

        assertEquals("Clicked with SikuliX", updatedStatus);
    }
}
