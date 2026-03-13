package examples;

import examples.pages.SikuliDemoPage;
import org.junit.jupiter.api.Test;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SikuliXWebPageHybridTest extends BaseUiTest {

    @Test
    void shouldUseSeleniumForThePageAndSikuliXForOneVisualStep() throws Exception {
        // Selenium browser'ı açar ve normal sayfa akışını yönetir.
        SikuliDemoPage page = new SikuliDemoPage(driver, wait);

        // Demo internet bağlantısına bağlı olmasın diye local bir HTML dosyası kullanıyoruz.
        page.open();

        // Adım 1: Selenium sayfanın açıldığını ve DOM'a erişebildiğini doğrular.
        assertEquals("Waiting for click", page.waitForInitialStatus());

        // Adım 2: Selenium butonu DOM üzerinden bulur.
        // Sonra bu butonun ekranda render edilmiş görüntüsünü alırız.
        // Bu görüntü SikuliX için arama pattern'i olur.
        byte[] buttonBytes = page.actionButtonScreenshot();
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
        assertEquals("Clicked with SikuliX", page.waitForClickedStatus());
    }
}
