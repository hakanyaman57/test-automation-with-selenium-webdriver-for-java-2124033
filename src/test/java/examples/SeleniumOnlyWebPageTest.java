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
        // Bu sürüm yalnızca Selenium kullanır.
        // Piksel tabanlı arama yoktur, bu yüzden SikuliX iznine ihtiyaç duymaz.
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);

        // Hibrit örnekte kullanılan local sayfanın aynısını tekrar kullanıyoruz.
        // Böylece eğitim sırasında karşılaştırma kolay olur:
        // aynı sayfa, aynı buton, farklı otomasyon yaklaşımı.
        Path demoPage = Path.of("src", "test", "resources", "pages", "sikulix-web-demo.html").toAbsolutePath();
        driver.get(demoPage.toUri().toString());

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Selenium butonu doğrudan DOM içinden id ile bulur.
        // SikuliX'ten temel fark da budur:
        // Selenium HTML elementleriyle çalışır, ekrandaki piksellerle değil.
        WebElement actionButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("sikuli-button")));

        // Tıklamadan önce sayfanın ilk halini kısa süre görünür bırakır.
        Thread.sleep(BEFORE_CLICK_PAUSE.toMillis());
        actionButton.click();

        // DOM üzerinden yapılan tıklamadan sonra durum metninin değiştiğini doğrularız.
        WebElement status = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("status")));
        assertEquals("Clicked with SikuliX", status.getText());

        // Sonucu canlı demoda görebilmek için browser'ı kısa süre açık bırakır.
        // Bu bekleme sunum içindir; normal hızlı test akışı için gerekli değildir.
        Thread.sleep(DEMO_PAUSE.toMillis());
    }
}
