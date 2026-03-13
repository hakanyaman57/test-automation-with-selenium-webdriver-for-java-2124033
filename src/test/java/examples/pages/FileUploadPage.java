package examples.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FileUploadPage {

    private static final String PAGE_URL = "https://the-internet.herokuapp.com/upload";
    private static final By FILE_INPUT = By.id("file-upload");
    private static final By FILE_SUBMIT = By.id("file-submit");
    private static final By UPLOADED_FILES = By.id("uploaded-files");
    private static final By UPLOAD_SUCCESS_HEADING = By.cssSelector("div.content h3");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public FileUploadPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void open() {
        driver.get(PAGE_URL);
    }

    public WebElement waitForFileInput() {
        return wait.until(ExpectedConditions.elementToBeClickable(FILE_INPUT));
    }

    public void submitUpload() {
        driver.findElement(FILE_SUBMIT).click();
    }

    public String waitForUploadedFileName() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(UPLOADED_FILES)).getText();
    }

    public String waitForUploadSuccessHeading() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(UPLOAD_SUCCESS_HEADING)).getText();
    }
}
