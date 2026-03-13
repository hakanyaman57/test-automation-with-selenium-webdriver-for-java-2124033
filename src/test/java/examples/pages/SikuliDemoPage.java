package examples.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Path;

public class SikuliDemoPage {

    private static final By STATUS = By.id("status");
    private static final By ACTION_BUTTON = By.id("sikuli-button");
    private static final String INITIAL_STATUS = "Waiting for click";
    private static final String CLICKED_STATUS = "Clicked with SikuliX";

    private final WebDriver driver;
    private final WebDriverWait wait;

    public SikuliDemoPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void open() {
        Path demoPage = Path.of("src", "test", "resources", "pages", "sikulix-web-demo.html").toAbsolutePath();
        driver.get(demoPage.toUri().toString());
    }

    public String waitForInitialStatus() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(STATUS)).getText();
    }

    public WebElement waitForActionButton() {
        return wait.until(ExpectedConditions.elementToBeClickable(ACTION_BUTTON));
    }

    public byte[] actionButtonScreenshot() {
        return waitForActionButton().getScreenshotAs(OutputType.BYTES);
    }

    public void clickActionButtonWithSelenium() {
        waitForActionButton().click();
    }

    public String waitForClickedStatus() {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(STATUS, CLICKED_STATUS));
        return driver.findElement(STATUS).getText();
    }
}
