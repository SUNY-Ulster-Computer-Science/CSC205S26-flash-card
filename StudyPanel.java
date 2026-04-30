import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * StudyPanel - flip card style quiz mode with score tracking.
 * Users see a question, flip the card to reveal the answer,
 * then mark themselves correct or incorrect.
 */
public class StudyPanel extends JPanel {

    // ── Dark mode palette ──────────────────────────────────────────
    private static final Color BG          = new Color(18,  18,  24);
    private static final Color CARD_FRONT  = new Color(30,  30,  42);
    private static final Color CARD_BACK   = new Color(25,  40,  60);
    private static final Color ACCENT      = new Color(99,  179, 237);
    private static final Color CORRECT_CLR = new Color(72,  199, 142);
    private static final Color WRONG_CLR   = new Color(252, 110, 110);
    private static final Color TEXT_PRI    = new Color(230, 230, 240);
    private static final Color TEXT_SEC    = new Color(140, 140, 160);
    private static final Color BTN_BG      = new Color(40,  40,  58);

    private FlashCardManager manager;
    private List<FlashCard> studyList;
    private int currentIndex = 0;
    private boolean isFlipped = false;
    private int correct = 0;
    private int incorrect = 0;

    // UI components
    private JPanel cardPanel;
    private JLabel cardLabel;
    private JLabel cardSubLabel;
    private JLabel progressLabel;
    private JLabel scoreLabel;
    private JProgressBar progressBar;
    private JButton flipBtn;
    private JButton correctBtn;
    private JButton wrongBtn;
    private JButton restartBtn;
    private JPanel feedbackPanel;

    public StudyPanel(FlashCardManager manager) {
        this.manager = manager;
        setBackground(BG);
        setLayout(new BorderLayout(0, 0));
        buildUI();
    }

    private void buildUI() {
        // ── Top bar ──────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(BG);
        topBar.setBorder(new EmptyBorder(20, 28, 10, 28));

        JLabel title = new JLabel("Study Mode");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(TEXT_PRI);

        scoreLabel = new JLabel("✓ 0   ✗ 0");
        scoreLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        scoreLabel.setForeground(TEXT_SEC);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(scoreLabel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // ── Center: card + progress ──────────────────────────────
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(BG);
        center.setBorder(new EmptyBorder(10, 40, 10, 40));

        // Progress bar row
        JPanel progressRow = new JPanel(new BorderLayout(8, 0));
        progressRow.setBackground(BG);
        progressRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        progressLabel = new JLabel("Card 0 of 0");
        progressLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        progressLabel.setForeground(TEXT_SEC);

        progressBar = new JProgressBar(0, 1);
        progressBar.setValue(0);
        progressBar.setPreferredSize(new Dimension(0, 6));
        progressBar.setForeground(ACCENT);
        progressBar.setBackground(new Color(45, 45, 60));
        progressBar.setBorderPainted(false);

        progressRow.add(progressLabel, BorderLayout.WEST);
        progressRow.add(progressBar, BorderLayout.CENTER);
        center.add(progressRow);
        center.add(Box.createVerticalStrut(18));

        // Flip card panel
        cardPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setBackground(CARD_FRONT);
        cardPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
        cardPanel.setPreferredSize(new Dimension(500, 260));
        cardPanel.setBorder(new EmptyBorder(36, 36, 36, 36));
        cardPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        cardSubLabel = new JLabel("QUESTION");
        cardSubLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        cardSubLabel.setForeground(ACCENT);
        cardSubLabel.setHorizontalAlignment(SwingConstants.CENTER);

        cardLabel = new JLabel("<html><div style='text-align:center'>Click \"Start Session\" to begin studying.</div></html>");
        cardLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        cardLabel.setForeground(TEXT_PRI);
        cardLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cardLabel.setVerticalAlignment(SwingConstants.CENTER);

        JPanel cardInner = new JPanel(new BorderLayout(0, 12));
        cardInner.setOpaque(false);
        cardInner.add(cardSubLabel, BorderLayout.NORTH);
        cardInner.add(cardLabel, BorderLayout.CENTER);
        cardPanel.add(cardInner, BorderLayout.CENTER);

        // Click card to flip
        cardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (studyList != null && !studyList.isEmpty() && currentIndex < studyList.size()) {
                    flipCard();
                }
            }
        });

        center.add(cardPanel);
        center.add(Box.createVerticalStrut(16));

        // Flip hint label
        JLabel hintLabel = new JLabel("↩  Click card or press Flip to reveal answer");
        hintLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        hintLabel.setForeground(TEXT_SEC);
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(hintLabel);

        add(center, BorderLayout.CENTER);

        // ── Bottom buttons ───────────────────────────────────────
        JPanel bottom = new JPanel();
        bottom.setBackground(BG);
        bottom.setBorder(new EmptyBorder(10, 28, 24, 28));
        bottom.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 0));

        flipBtn    = makeBtn("Flip Card",    ACCENT,     Color.BLACK);
        correctBtn = makeBtn("✓  Got it",    CORRECT_CLR, Color.BLACK);
        wrongBtn   = makeBtn("✗  Missed it", WRONG_CLR,  Color.BLACK);
        restartBtn = makeBtn("↺  Restart",   BTN_BG,     TEXT_PRI);

        flipBtn.addActionListener(e -> flipCard());
        correctBtn.addActionListener(e -> markAnswer(true));
        wrongBtn.addActionListener(e -> markAnswer(false));
        restartBtn.addActionListener(e -> startSession());

        bottom.add(restartBtn);
        bottom.add(flipBtn);
        bottom.add(correctBtn);
        bottom.add(wrongBtn);

        // feedback panel (shown after session ends)
        feedbackPanel = new JPanel();
        feedbackPanel.setBackground(BG);
        feedbackPanel.setVisible(false);

        add(bottom, BorderLayout.SOUTH);

        setAnswerButtonsEnabled(false);
        updateCardDisplay();
    }

    // ── Public: called when tab is selected so cards are fresh ───
    public void startSession() {
        if (manager.getCards().isEmpty()) {
            cardLabel.setText("<html><div style='text-align:center'>No cards yet!<br>Add cards in the Manage tab first.</div></html>");
            cardSubLabel.setText("EMPTY DECK");
            progressLabel.setText("Card 0 of 0");
            progressBar.setMaximum(1);
            progressBar.setValue(0);
            setAnswerButtonsEnabled(false);
            flipBtn.setEnabled(false);
            return;
        }

        studyList = new ArrayList<>(manager.getCards());
        Collections.shuffle(studyList);
        currentIndex = 0;
        correct = 0;
        incorrect = 0;
        isFlipped = false;
        flipBtn.setEnabled(true);

        progressBar.setMaximum(studyList.size());
        updateCardDisplay();
        updateScore();
        setAnswerButtonsEnabled(false);
        cardPanel.setBackground(CARD_FRONT);
    }

    private void flipCard() {
        if (studyList == null || currentIndex >= studyList.size()) return;
        isFlipped = !isFlipped;
        FlashCard card = studyList.get(currentIndex);

        if (isFlipped) {
            cardSubLabel.setText("ANSWER");
            cardSubLabel.setForeground(CORRECT_CLR);
            cardLabel.setText("<html><div style='text-align:center'>" + card.getAnswer() + "</div></html>");
            cardPanel.setBackground(CARD_BACK);
            setAnswerButtonsEnabled(true);
            flipBtn.setText("Show Question");
        } else {
            cardSubLabel.setText("QUESTION");
            cardSubLabel.setForeground(ACCENT);
            cardLabel.setText("<html><div style='text-align:center'>" + card.getQuestion() + "</div></html>");
            cardPanel.setBackground(CARD_FRONT);
            setAnswerButtonsEnabled(false);
            flipBtn.setText("Flip Card");
        }
        cardPanel.repaint();
    }

    private void markAnswer(boolean wasCorrect) {
        if (wasCorrect) correct++; else incorrect++;
        currentIndex++;
        isFlipped = false;
        flipBtn.setText("Flip Card");
        updateScore();

        if (currentIndex >= studyList.size()) {
            showResults();
        } else {
            setAnswerButtonsEnabled(false);
            cardPanel.setBackground(CARD_FRONT);
            updateCardDisplay();
        }
    }

    private void showResults() {
        int total = correct + incorrect;
        int pct = total == 0 ? 0 : (correct * 100 / total);
        String msg = String.format(
            "<html><div style='text-align:center'>"
            + "<b style='font-size:16px'>Session Complete!</b><br><br>"
            + "✓ Correct: %d &nbsp;&nbsp; ✗ Missed: %d<br>"
            + "Score: %d%%<br><br>"
            + "<span style='color:#99aabb'>Click ↺ Restart to go again</span>"
            + "</div></html>", correct, incorrect, pct);

        cardLabel.setText(msg);
        cardSubLabel.setText("RESULTS");
        cardSubLabel.setForeground(pct >= 70 ? CORRECT_CLR : WRONG_CLR);
        cardPanel.setBackground(new Color(28, 28, 42));
        progressBar.setValue(total);
        setAnswerButtonsEnabled(false);
        flipBtn.setEnabled(false);
    }

    private void updateCardDisplay() {
        if (studyList == null || studyList.isEmpty()) return;
        if (currentIndex >= studyList.size()) return;

        FlashCard card = studyList.get(currentIndex);
        cardSubLabel.setText("QUESTION");
        cardSubLabel.setForeground(ACCENT);
        cardLabel.setText("<html><div style='text-align:center'>" + card.getQuestion() + "</div></html>");
        progressLabel.setText("Card " + (currentIndex + 1) + " of " + studyList.size());
        progressBar.setValue(currentIndex);
        cardPanel.repaint();
    }

    private void updateScore() {
        scoreLabel.setText("<html><span style='color:#48c78e'>✓ " + correct
            + "</span> &nbsp; <span style='color:#fc6e6e'>✗ " + incorrect + "</span></html>");
    }

    private void setAnswerButtonsEnabled(boolean enabled) {
        correctBtn.setEnabled(enabled);
        wrongBtn.setEnabled(enabled);
    }

    private JButton makeBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isEnabled() ? bg : bg.darker());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 42));
        return btn;
    }
}
