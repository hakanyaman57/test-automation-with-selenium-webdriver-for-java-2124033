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
        // Selenium browser'ı açar ve normal sayfa akışını yönetir.
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);

        // Demo internet bağlantısına bağlı olmasın diye local bir HTML dosyası kullanıyoruz.
        Path demoPage = Path.of("src", "test", "resources", "pages", "sikulix-web-demo.html").toAbsolutePath();
        driver.get(demoPage.toUri().toString());

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Adım 1: Selenium sayfanın açıldığını ve DOM'a erişebildiğini doğrular.
        WebElement status = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("status")));
        assertEquals("Waiting for click", status.getText());

        // Adım 2: Selenium butonu DOM üzerinden bulur.
        // Sonra bu butonun ekranda render edilmiş görüntüsünü alırız.
        // Bu görüntü SikuliX için arama pattern'i olur.
        WebElement actionButton = driver.findElement(By.id("sikuli-button"));
        byte[] buttonBytes = actionButton.getScreenshotAs(OutputType.BYTES);
        BufferedImage buttonImage = ImageIO.read(new ByteArrayInputStream(buttonBytes));

        // Adım 3: SikuliX bu görüntüyü gerçek ekranda arar ve tıklar.
        // Burası "görsel otomasyon" kısmıdır:
        // SikuliX DOM ile ilgilenmez, sadece ekrandaki piksellere bakar.
        Screen screen = new Screen();
        Pattern buttonPattern = new Pattern(buttonImage).similar(0.95f);
        screen.wait(buttonPattern, 10);
        screen.click(buttonPattern);

        // Adım 4: Görsel tıklamadan sonra tekrar Selenium'a dönüp sonucu doğrularız.
        // Tıklama başarılıysa sayfadaki durum metni değişir.
        String updatedStatus = wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("status"),
                "Clicked with SikuliX")) ? driver.findElement(By.id("status")).getText() : "";

        assertEquals("Clicked with SikuliX", updatedStatus);
    }
}
