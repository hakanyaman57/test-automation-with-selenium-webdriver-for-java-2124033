package examples;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.awt.GraphicsEnvironment;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SikuliXQuickDemoTest {

    @Test
    void shouldClickTheRenderedButtonWithSikuliX() throws Exception {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "Requires a visible desktop session.");
        assertTrue(SikuliXQuickDemo.runDemo(Duration.ofSeconds(8)));
    }
}
