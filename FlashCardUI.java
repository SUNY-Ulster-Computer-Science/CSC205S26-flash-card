import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class FlashCardUI extends JFrame {

    // Dark palette
    static final Color BG        = new Color(18,  18,  24);
    static final Color SURFACE   = new Color(28,  28,  40);
    static final Color SURFACE2  = new Color(38,  38,  54);
    static final Color ACCENT    = new Color(99,  179, 237);
    static final Color ACCENT2   = new Color(129, 140, 248);
    static final Color TEXT_PRI  = new Color(230, 230, 240);
    static final Color TEXT_SEC  = new Color(130, 130, 155);
    static final Color DANGER    = new Color(252, 110, 110);
    static final Color SUCCESS   = new Color(72,  199, 142);
    static final Color BORDER    = new Color(50,  50,  68);

    private FlashCardManager manager;
    private StudyPanel studyPanel;

    private DefaultListModel<FlashCard> listModel;
    private JList<FlashCard> cardList;
    private JTextField questionField;
    private JTextArea answerArea;
    private JLabel statusLabel;
    private JLabel cardCountLabel;

    public FlashCardUI() {
        manager = new FlashCardManager();
        try { manager.load(); } catch (IOException ignored) {}
        applyGlobalDarkTheme();
        buildWindow();
    }

    private void applyGlobalDarkTheme() {
        UIManager.put("TabbedPane.background",       BG);
        UIManager.put("TabbedPane.foreground",       TEXT_PRI);
        UIManager.put("TabbedPane.selected",         SURFACE2);
        UIManager.put("TabbedPane.contentAreaColor", BG);
        UIManager.put("TabbedPane.shadow",           BORDER);
        UIManager.put("TabbedPane.darkShadow",       BORDER);
        UIManager.put("TabbedPane.light",            BORDER);
        UIManager.put("TabbedPane.highlight",        SURFACE2);
        UIManager.put("ScrollBar.background",        SURFACE);
        UIManager.put("ScrollBar.thumb",             SURFACE2);
        UIManager.put("ScrollBar.track",             SURFACE);
        UIManager.put("List.background",             SURFACE);
        UIManager.put("List.foreground",             TEXT_PRI);
        UIManager.put("List.selectionBackground",    ACCENT.darker().darker());
        UIManager.put("List.selectionForeground",    TEXT_PRI);
    }

    private void buildWindow() {
        setTitle("FlashCard App");
        setSize(780, 560);
        setMinimumSize(new Dimension(660, 480));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(BG);
        tabs.setForeground(TEXT_PRI);
        tabs.setFont(new Font("SansSerif", Font.BOLD, 14));
        tabs.setBorder(BorderFactory.createEmptyBorder());

        tabs.addTab("  Manage Cards  ", buildManagePanel());
        studyPanel = new StudyPanel(manager);
        tabs.addTab("  Study  ", studyPanel);

        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 1) studyPanel.startSession();
        });

        add(tabs, BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        setVisible(true);
        refreshList();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SURFACE);
        header.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(14, 24, 14, 24)
        ));
        JLabel appName = new JLabel("⚡ FlashCard");
        appName.setFont(new Font("SansSerif", Font.BOLD, 24));
        appName.setForeground(ACCENT);
        cardCountLabel = new JLabel(getCardCountText());
        cardCountLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cardCountLabel.setForeground(TEXT_SEC);
        header.add(appName, BorderLayout.WEST);
        header.add(cardCountLabel, BorderLayout.EAST);
        return header;
    }

    private JPanel buildManagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        listModel = new DefaultListModel<>();
        cardList  = new JList<>(listModel);
        cardList.setBackground(SURFACE);
        cardList.setForeground(TEXT_PRI);
        cardList.setSelectionBackground(new Color(60, 80, 110));
        cardList.setSelectionForeground(TEXT_PRI);
        cardList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cardList.setFixedCellHeight(44);
        cardList.setBorder(new EmptyBorder(4, 8, 4, 8));
        cardList.setCellRenderer(new CardListRenderer());

        JScrollPane listScroll = new JScrollPane(cardList);
        listScroll.setPreferredSize(new Dimension(220, 0));
        listScroll.setBackground(SURFACE);
        listScroll.getViewport().setBackground(SURFACE);
        listScroll.setBorder(new MatteBorder(0, 0, 0, 1, BORDER));

        panel.add(listScroll, BorderLayout.WEST);
        panel.add(buildEditPanel(), BorderLayout.CENTER);

        cardList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = cardList.getSelectedIndex();
                if (idx != -1) {
                    FlashCard c = manager.getCards().get(idx);
                    questionField.setText(c.getQuestion());
                    answerArea.setText(c.getAnswer());
                }
            }
        });
        return panel;
    }

    private JPanel buildEditPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(24, 28, 16, 28));

        panel.add(makeFieldLabel("Question"));
        panel.add(Box.createVerticalStrut(6));
        questionField = new JTextField();
        styleTextField(questionField);
        questionField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        panel.add(questionField);
        panel.add(Box.createVerticalStrut(16));

        panel.add(makeFieldLabel("Answer"));
        panel.add(Box.createVerticalStrut(6));
        answerArea = new JTextArea(4, 0);
        answerArea.setLineWrap(true);
        answerArea.setWrapStyleWord(true);
        answerArea.setBackground(SURFACE2);
        answerArea.setForeground(TEXT_PRI);
        answerArea.setCaretColor(ACCENT);
        answerArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane ansScroll = new JScrollPane(answerArea);
        ansScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        ansScroll.setBackground(SURFACE2);
        ansScroll.getViewport().setBackground(SURFACE2);
        ansScroll.setBorder(BorderFactory.createLineBorder(BORDER));
        panel.add(ansScroll);
        panel.add(Box.createVerticalStrut(24));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setBackground(BG);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JButton addBtn    = makeActionBtn("+ Add",    ACCENT,   Color.BLACK);
        JButton updateBtn = makeActionBtn("✎ Update", ACCENT2,  Color.BLACK);
        JButton deleteBtn = makeActionBtn("✕ Delete", DANGER,   Color.WHITE);
        JButton saveBtn   = makeActionBtn("💾 Save",  SUCCESS,  Color.BLACK);
        JButton clearBtn  = makeActionBtn("Clear",    SURFACE2, TEXT_SEC);

        addBtn.addActionListener(e    -> addCard());
        updateBtn.addActionListener(e -> updateCard());
        deleteBtn.addActionListener(e -> deleteCard());
        saveBtn.addActionListener(e   -> saveCards());
        clearBtn.addActionListener(e  -> clearFields());

        btnRow.add(addBtn); btnRow.add(updateBtn); btnRow.add(deleteBtn);
        btnRow.add(saveBtn); btnRow.add(clearBtn);
        panel.add(btnRow);
        return panel;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(SURFACE);
        bar.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, BORDER),
            new EmptyBorder(6, 20, 6, 20)
        ));
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_SEC);
        bar.add(statusLabel, BorderLayout.WEST);
        return bar;
    }

    private void addCard() {
        String q = questionField.getText().trim();
        String a = answerArea.getText().trim();
        if (q.isEmpty() || a.isEmpty()) { setStatus("Please fill in both question and answer.", true); return; }
        manager.addCard(q, a);
        refreshList(); clearFields();
        setStatus("Card added.", false);
    }

    private void updateCard() {
        int idx = cardList.getSelectedIndex();
        if (idx == -1) { setStatus("Select a card to update.", true); return; }
        String q = questionField.getText().trim();
        String a = answerArea.getText().trim();
        if (q.isEmpty() || a.isEmpty()) { setStatus("Fields cannot be empty.", true); return; }
        manager.updateCard(idx, q, a);
        refreshList();
        setStatus("Card updated.", false);
    }

    private void deleteCard() {
        int idx = cardList.getSelectedIndex();
        if (idx == -1) { setStatus("Select a card to delete.", true); return; }
        manager.deleteCard(idx);
        refreshList(); clearFields();
        setStatus("Card deleted.", false);
    }

    private void saveCards() {
        try { manager.save(); setStatus("Cards saved to disk.", false); }
        catch (Exception ex) { setStatus("Error saving: " + ex.getMessage(), true); }
    }

    private void clearFields() {
        questionField.setText("");
        answerArea.setText("");
        cardList.clearSelection();
    }

    private void refreshList() {
        listModel.clear();
        for (FlashCard c : manager.getCards()) listModel.addElement(c);
        cardCountLabel.setText(getCardCountText());
    }

    private void setStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        statusLabel.setForeground(isError ? DANGER : SUCCESS);
        Timer t = new Timer(3000, e -> { statusLabel.setText("Ready"); statusLabel.setForeground(TEXT_SEC); });
        t.setRepeats(false); t.start();
    }

    private String getCardCountText() {
        int n = manager.getCardCount();
        return n + (n == 1 ? " card" : " cards") + " in deck";
    }

    private JLabel makeFieldLabel(String text) {
        JLabel lbl = new JLabel(text.toUpperCase());
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(TEXT_SEC);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void styleTextField(JTextField field) {
        field.setBackground(SURFACE2);
        field.setForeground(TEXT_PRI);
        field.setCaretColor(ACCENT);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(8, 12, 8, 12)
        ));
    }

    private JButton makeActionBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isEnabled() ? bg : bg.darker());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 38));
        return btn;
    }

    private class CardListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean hasFocus) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
            lbl.setBackground(isSelected ? new Color(50, 70, 100) : SURFACE);
            lbl.setForeground(TEXT_PRI);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
            String q = value.toString();
            lbl.setText(q.length() > 30 ? q.substring(0, 28) + "…" : q);
            if (isSelected) {
                lbl.setBorder(new CompoundBorder(
                    new MatteBorder(0, 3, 1, 0, ACCENT),
                    new EmptyBorder(8, 8, 8, 10)
                ));
            } else {
                lbl.setBorder(new CompoundBorder(
                    new MatteBorder(0, 0, 1, 0, BORDER),
                    new EmptyBorder(8, 10, 8, 10)
                ));
            }
            return lbl;
        }
    }
}
