package examples;

import examples.pages.FileUploadPage;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.sikuli.script.Key;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

/**
 * Gerçek hayata yakın hibrit senaryo:
 * - Selenium web sayfasını açar
 * - SikuliX sayfadaki "Choose File" kontrolünü görsel olarak bulur
 * - SikuliX native dosya seçme penceresini yönetir
 *
 * Bu testin sağlıklı çalışması için etkileşimli bir masaüstü oturumu ve
 * işletim sistemi temanıza göre alınmış görsel şablonlar gerekir.
 */
class SikuliXNativeFileUploadTest extends BaseUiTest {

    @Override
    protected boolean shouldQuitDriverAfterEach() {
        return false;
    }

    @Test
    void uploadFileUsingNativeDialogWithSikuliX() throws Exception {
        FileUploadPage page = new FileUploadPage(driver, wait);

        page.open();

        // Selenium file input elemanını yine DOM üzerinden bulur.
        // Ancak bazı driver/browser kombinasyonları input[type=file] için
        // Selenium click() çağrısını reddedebilir.
        // Bu yüzden elemanın ekrandaki görüntüsünü alıp SikuliX pattern'ine çeviriyoruz
        // ve tıklamayı görsel olarak yaptırıyoruz.
        WebElement fileInput = page.waitForFileInput();

        Screen screen = new Screen();
        BufferedImage chooseFileImage = ImageIO.read(new ByteArrayInputStream(fileInput.getScreenshotAs(OutputType.BYTES)));
        Pattern chooseFileButton = new Pattern(chooseFileImage).similar(0.95f);

        screen.wait(chooseFileButton, 10);
        screen.click(chooseFileButton); // Native dosya seçme penceresini görsel olarak açar
        Thread.sleep(1500);
        String capturePath = screen.capture().save("target/sikulix-debug", "native-dialog");
        System.out.println("Saved native dialog capture to: " + capturePath);

        // Bu görseller browser içinden değil,
        // native Windows/macOS dosya seçme penceresinden alınmış olmalıdır.
        Pattern picturesLocation = resourcePattern("/images/pictures.png", 0.95f);
        Pattern fileNameInput = resourcePattern("/images/file_name_input.png", 0.95f);
        Pattern openButton = resourcePattern("/images/open_button.png", 0.95f);

        Path fileToUpload = Path.of("README.md").toAbsolutePath();

        Match bestPicturesMatch = screen.exists(picturesLocation, 10);
        if (bestPicturesMatch == null) {
            System.out.printf("pictures.png: no match found at similarity %.2f%n", picturesLocation.getSimilar());
            throw new IllegalStateException("SikuliX could not find pictures.png on screen.");
        }

        List<Match> pictureMatches = screen.findAllList(picturesLocation);
        System.out.printf(
                "pictures.png: found %d candidate(s); best score=%.4f at x=%d y=%d w=%d h=%d%n",
                pictureMatches.size(),
                bestPicturesMatch.getScore(),
                bestPicturesMatch.x,
                bestPicturesMatch.y,
                bestPicturesMatch.w,
                bestPicturesMatch.h
        );
        for (int i = 0; i < Math.min(3, pictureMatches.size()); i++) {
            Match candidate = pictureMatches.get(i);
            System.out.printf(
                    "pictures.png: candidate[%d] score=%.4f at x=%d y=%d w=%d h=%d%n",
                    i,
                    candidate.getScore(),
                    candidate.x,
                    candidate.y,
                    candidate.w,
                    candidate.h
            );
        }

        screen.click(bestPicturesMatch);
        screen.wait(fileNameInput, 10);
        screen.click(fileNameInput);
        screen.type(fileToUpload.toString());
        Thread.sleep(1000);

        Match bestOpenMatch = screen.exists(openButton, 5);
        if (bestOpenMatch == null) {
            System.out.printf("open_button.png: no match found at similarity %.2f%n", openButton.getSimilar());
            System.out.println("Birçok native dosya penceresi ENTER tuşunu Open olarak kabul ettiği için ENTER ile devam ediliyor.");
            screen.type(Key.ENTER);
        } else {
            List<Match> openMatches = screen.findAllList(openButton);
            System.out.printf(
                    "open_button.png: found %d candidate(s); best score=%.4f at x=%d y=%d w=%d h=%d%n",
                    openMatches.size(),
                    bestOpenMatch.getScore(),
                    bestOpenMatch.x,
                    bestOpenMatch.y,
                    bestOpenMatch.w,
                    bestOpenMatch.h
            );
            for (int i = 0; i < Math.min(3, openMatches.size()); i++) {
                Match candidate = openMatches.get(i);
                System.out.printf(
                        "open_button.png: candidate[%d] score=%.4f at x=%d y=%d w=%d h=%d%n",
                        i,
                        candidate.getScore(),
                        candidate.x,
                        candidate.y,
                        candidate.w,
                        candidate.h
                );
            }

            screen.click(bestOpenMatch);
        }

        page.submitUpload();

        String uploaded = page.waitForUploadedFileName();
        String successHeading = page.waitForUploadSuccessHeading();

        assertTrue(uploaded.contains("README.md"));
        assertEquals("File Uploaded!", successHeading);
    }

    private Pattern resourcePattern(String resourcePath, float similarity) {
        var resourceUrl = Objects.requireNonNull(
                getClass().getResource(resourcePath),
                "Missing test resource: " + resourcePath
        );
        Pattern pattern = new Pattern(resourceUrl).similar(similarity);
        System.out.printf("Loaded pattern %s from %s with similarity %.2f%n", resourcePath, resourceUrl, similarity);
        return pattern;
    }
}
