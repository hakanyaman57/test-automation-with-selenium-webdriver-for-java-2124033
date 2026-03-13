package examples;

import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;
import org.sikuli.script.SikuliXception;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class SikuliXQuickDemo {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(8);

    private SikuliXQuickDemo() {
    }

    public static void main(String[] args) throws Exception {
        boolean success = runDemo(DEFAULT_TIMEOUT);
        if (!success) {
            throw new IllegalStateException("SikuliX demo reached the window but did not activate the success state.");
        }
        System.out.println("SikuliX quick demo completed successfully.");
    }

    public static boolean runDemo(Duration timeout) throws Exception {
        if (GraphicsEnvironment.isHeadless() || Screen.isHeadless()) {
            throw new IllegalStateException("SikuliX quick demo requires a visible desktop session.");
        }

        DemoWindow demoWindow = DemoWindow.show();
        try {
            Region appRegion = demoWindow.regionOnScreen();
            Pattern buttonPattern = new Pattern(demoWindow.buttonPattern()).similar(0.95f);
            Pattern successPattern = new Pattern(demoWindow.successPattern()).similar(0.95f);

            appRegion.wait(buttonPattern, seconds(timeout));
            appRegion.click(buttonPattern);
            appRegion.wait(successPattern, seconds(timeout));

            return demoWindow.awaitActivated(timeout);
        } catch (SikuliXception exception) {
            throw new IllegalStateException(
                    "SikuliX could not access the mouse/screen. On macOS, enable Screen Recording and Accessibility for your IDE or terminal.",
                    exception
            );
        } finally {
            demoWindow.close();
        }
    }

    private static double seconds(Duration timeout) {
        return timeout.toMillis() / 1000.0;
    }

    private static final class DemoWindow {

        private final JFrame frame;
        private final DemoPanel panel;

        private DemoWindow(JFrame frame, DemoPanel panel) {
            this.frame = frame;
            this.panel = panel;
        }

        static DemoWindow show() throws Exception {
            AtomicReference<DemoWindow> reference = new AtomicReference<>();

            SwingUtilities.invokeAndWait(() -> {
                DemoPanel panel = new DemoPanel();
                JFrame frame = new JFrame("SikuliX Quick Demo");
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                frame.setAlwaysOnTop(true);
                frame.setContentPane(panel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                frame.toFront();
                frame.requestFocus();

                reference.set(new DemoWindow(frame, panel));
            });

            Thread.sleep(700);
            return reference.get();
        }

        Region regionOnScreen() throws Exception {
            AtomicReference<Rectangle> reference = new AtomicReference<>();
            SwingUtilities.invokeAndWait(() -> {
                Point location = panel.getLocationOnScreen();
                Dimension size = panel.getSize();
                reference.set(new Rectangle(location.x, location.y, size.width, size.height));
            });
            return new Region(reference.get());
        }

        BufferedImage buttonPattern() throws Exception {
            AtomicReference<BufferedImage> reference = new AtomicReference<>();
            SwingUtilities.invokeAndWait(() -> reference.set(panel.renderButtonPattern()));
            return reference.get();
        }

        BufferedImage successPattern() throws Exception {
            AtomicReference<BufferedImage> reference = new AtomicReference<>();
            SwingUtilities.invokeAndWait(() -> reference.set(panel.renderSuccessPattern()));
            return reference.get();
        }

        boolean awaitActivated(Duration timeout) throws InterruptedException {
            return panel.awaitActivated(timeout);
        }

        void close() throws Exception {
            SwingUtilities.invokeAndWait(frame::dispose);
        }
    }

    private static final class DemoPanel extends JPanel {

        private static final Dimension PANEL_SIZE = new Dimension(520, 300);
        private static final Rectangle BUTTON_BOUNDS = new Rectangle(140, 140, 240, 58);
        private static final Rectangle SUCCESS_BOUNDS = new Rectangle(130, 226, 260, 38);
        private static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 28);
        private static final Font BODY_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 15);
        private static final Font BUTTON_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 24);
        private static final Font SUCCESS_FONT = new Font(Font.MONOSPACED, Font.BOLD, 18);

        private final CountDownLatch activatedLatch = new CountDownLatch(1);
        private boolean activated;

        DemoPanel() {
            setPreferredSize(PANEL_SIZE);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    if (BUTTON_BOUNDS.contains(event.getPoint())) {
                        activate();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = (Graphics2D) graphics.create();
            paintScene(g2, activated);
            g2.dispose();
        }

        BufferedImage renderButtonPattern() {
            return renderPattern(BUTTON_BOUNDS, false);
        }

        BufferedImage renderSuccessPattern() {
            return renderPattern(SUCCESS_BOUNDS, true);
        }

        boolean awaitActivated(Duration timeout) throws InterruptedException {
            return activatedLatch.await(timeout.toMillis(), TimeUnit.MILLISECONDS);
        }

        private void activate() {
            if (!activated) {
                activated = true;
                activatedLatch.countDown();
                repaint();
            }
        }

        private BufferedImage renderPattern(Rectangle logicalBounds, boolean activeState) {
            AffineTransform transform = scaleTransform();
            double scaleX = transform.getScaleX();
            double scaleY = transform.getScaleY();
            BufferedImage scene = renderScene(activeState, scaleX, scaleY);

            int x = (int) Math.round(logicalBounds.x * scaleX);
            int y = (int) Math.round(logicalBounds.y * scaleY);
            int width = (int) Math.round(logicalBounds.width * scaleX);
            int height = (int) Math.round(logicalBounds.height * scaleY);

            BufferedImage copy = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = copy.createGraphics();
            g2.drawImage(scene, 0, 0, width, height, x, y, x + width, y + height, null);
            g2.dispose();
            return copy;
        }

        private BufferedImage renderScene(boolean activeState, double scaleX, double scaleY) {
            int width = (int) Math.round(PANEL_SIZE.width * scaleX);
            int height = (int) Math.round(PANEL_SIZE.height * scaleY);

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();
            g2.scale(scaleX, scaleY);
            paintScene(g2, activeState);
            g2.dispose();
            return image;
        }

        private AffineTransform scaleTransform() {
            GraphicsConfiguration configuration = getGraphicsConfiguration();
            if (configuration == null) {
                return new AffineTransform();
            }
            return configuration.getDefaultTransform();
        }

        private void paintScene(Graphics2D g2, boolean activeState) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2.setPaint(new GradientPaint(0, 0, new Color(247, 242, 232), 0, PANEL_SIZE.height, new Color(234, 224, 204)));
            g2.fillRect(0, 0, PANEL_SIZE.width, PANEL_SIZE.height);

            g2.setColor(new Color(57, 61, 73));
            g2.setFont(TITLE_FONT);
            g2.drawString("SikuliX Quick Demo", 118, 78);

            g2.setColor(new Color(92, 98, 112));
            g2.setFont(BODY_FONT);
            g2.drawString("SikuliX ekrandaki turuncu butonu bulup tiklayacak.", 70, 110);

            drawButton(g2);

            if (activeState) {
                drawSuccessBadge(g2);
            }
        }

        private void drawButton(Graphics2D g2) {
            g2.setColor(new Color(0, 0, 0, 35));
            g2.fillRoundRect(BUTTON_BOUNDS.x, BUTTON_BOUNDS.y + 6, BUTTON_BOUNDS.width, BUTTON_BOUNDS.height, 24, 24);

            g2.setColor(new Color(220, 111, 52));
            g2.fillRoundRect(BUTTON_BOUNDS.x, BUTTON_BOUNDS.y, BUTTON_BOUNDS.width, BUTTON_BOUNDS.height, 24, 24);

            g2.setColor(new Color(255, 246, 235));
            g2.setFont(BUTTON_FONT);
            drawCenteredText(g2, "START DEMO", BUTTON_BOUNDS);
        }

        private void drawSuccessBadge(Graphics2D g2) {
            g2.setColor(new Color(35, 126, 77));
            g2.fillRoundRect(SUCCESS_BOUNDS.x, SUCCESS_BOUNDS.y, SUCCESS_BOUNDS.width, SUCCESS_BOUNDS.height, 20, 20);

            g2.setColor(new Color(240, 255, 244));
            g2.setFont(SUCCESS_FONT);
            drawCenteredText(g2, "CLICK CONFIRMED", SUCCESS_BOUNDS);
        }

        private void drawCenteredText(Graphics2D g2, String text, Rectangle bounds) {
            int textWidth = g2.getFontMetrics().stringWidth(text);
            int textHeight = g2.getFontMetrics().getAscent();
            int x = bounds.x + (bounds.width - textWidth) / 2;
            int y = bounds.y + ((bounds.height - g2.getFontMetrics().getHeight()) / 2) + textHeight;
            g2.drawString(text, x, y);
        }
    }
}
