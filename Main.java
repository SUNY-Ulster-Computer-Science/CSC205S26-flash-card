import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Use system look and feel as base, then apply our dark theme on top
        SwingUtilities.invokeLater(() -> {
            try {
                // Cross-platform L&F gives us consistent painting that our custom
                // colors can override predictably
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}
            new FlashCardUI();
        });
    }
}
