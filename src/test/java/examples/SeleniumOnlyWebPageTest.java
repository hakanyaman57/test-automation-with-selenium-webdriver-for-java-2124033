package examples;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import examples.pages.SikuliDemoPage;

class SeleniumOnlyWebPageTest extends BaseUiTest {

    private static final Duration BEFORE_CLICK_PAUSE = Duration.ofSeconds(2);
    private static final Duration DEMO_PAUSE = Duration.ofSeconds(3);

    @Test
    void shouldClickTheSameButtonUsingOnlySelenium() throws Exception {
        // Bu sürüm yalnızca Selenium kullanır.
        // Piksel tabanlı arama yoktur, bu yüzden SikuliX iznine ihtiyaç duymaz.
        SikuliDemoPage page = new SikuliDemoPage(driver, wait);

        // Hibrit örnekte kullanılan local sayfanın aynısını tekrar kullanıyoruz.
        // Böylece eğitim sırasında karşılaştırma kolay olur:
        // aynı sayfa, aynı buton, farklı otomasyon yaklaşımı.
        page.open();

        // Selenium butonu doğrudan DOM içinden id ile bulur.
        // SikuliX'ten temel fark da budur:
        // Selenium HTML elementleriyle çalışır, ekrandaki piksellerle değil.
        assertEquals("Waiting for click", page.waitForInitialStatus());

        // Tıklamadan önce sayfanın ilk halini kısa süre görünür bırakır.
        Thread.sleep(BEFORE_CLICK_PAUSE.toMillis());
        page.clickActionButtonWithSelenium();

        // DOM üzerinden yapılan tıklamadan sonra durum metninin değiştiğini doğrularız.
        assertEquals("Clicked with SikuliX", page.waitForClickedStatus());

        // Sonucu canlı demoda görebilmek için browser'ı kısa süre açık bırakır.
        // Bu bekleme sunum içindir; normal hızlı test akışı için gerekli değildir.
        Thread.sleep(DEMO_PAUSE.toMillis());
    }
}
