package dev.ahwz.ipd.ui;


import com.formdev.flatlaf.FlatDarkLaf;

import dev.ahwz.ipd.engine.Tournament;
import dev.ahwz.ipd.model.PayoffMatrix;
import dev.ahwz.ipd.model.Strategy;
import dev.ahwz.ipd.registry.StrategyRegistry;
import dev.ahwz.ipd.strategies.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Main application window for the Iterated Prisoner's Dilemma Simulator.
 * Uses FlatLaf dark theme with a three-panel layout matching the wireframe.
 * <p>
 * Layout:
 * ┌──────────────────────────────────────────┐
 * │  Header (logo + title)                   │
 * ├────────────┬──────────────┬──────────────┤
 * │ Strategies │  Tournament  │   Results    │
 * │   Panel    │  Setup Panel │   Panel      │
 * ├────────────┴──────────────┴──────────────┤
 * │  Status bar + progress bar               │
 * └──────────────────────────────────────────┘
 */
public class SwingGui extends JFrame {

    // ── Accent colour used throughout ──────────────────────────────────────
    private static final Color ACCENT = new Color(0x4FC3F7);   // cyan-blue
    private static final Color ACCENT_DIM = new Color(0x1A3A4A);
    private static final Color SURFACE = new Color(0x1E2533);
    private static final Color SURFACE2 = new Color(0x252D3D);
    private static final Color BORDER_COLOR = new Color(0x2E3A50);
    private static final Color TEXT_MUTED = new Color(0x8899AA);
    private static final Color TEXT_MAIN = new Color(0xDDEEFF);

    // ── State ───────────────────────────────────────────────────────────────
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private DefaultTableModel resultsModel;

    // Strategy checkboxes (name -> checkbox)
    private final java.util.Map<String, JCheckBox> strategyCheckboxes = new java.util.LinkedHashMap<>();
    private JTextField roundsField;
    private JTextField noiseField;
    private JTextField rewardField;
    private JTextField temptationField;
    private JTextField punishmentField;
    private JTextField suckerField;

    // ── Entry point (for standalone testing) ───────────────────────────────
    public static void main(String[] args) {
        // Apply FlatLaf BEFORE any Swing objects are created
        FlatDarkLaf.setup();
        customizeFlatLaf();

        SwingUtilities.invokeLater(() -> {
            SwingGui w = new SwingGui();
            w.setVisible(true);
        });
    }

    /**
     * Fine-tune FlatLaf UI defaults to match our dark sci-fi palette.
     */
    private static void customizeFlatLaf() {
        UIManager.put("Panel.background", SURFACE);
        UIManager.put("RootPane.background", SURFACE);
        UIManager.put("ScrollPane.background", SURFACE);
        UIManager.put("Viewport.background", SURFACE);
        UIManager.put("Table.background", SURFACE2);
        UIManager.put("Table.alternateRowColor", SURFACE);
        UIManager.put("Table.selectionBackground", ACCENT_DIM);
        UIManager.put("Table.selectionForeground", TEXT_MAIN);
        UIManager.put("TableHeader.background", SURFACE);
        UIManager.put("TableHeader.foreground", ACCENT);
        UIManager.put("TextField.background", SURFACE2);
        UIManager.put("TextField.foreground", TEXT_MAIN);
        UIManager.put("TextField.caretForeground", ACCENT);
        UIManager.put("CheckBox.foreground", TEXT_MAIN);
        UIManager.put("Button.background", SURFACE2);
        UIManager.put("Button.foreground", TEXT_MAIN);
        UIManager.put("ProgressBar.foreground", ACCENT);
        UIManager.put("ProgressBar.background", SURFACE2);
        UIManager.put("Label.foreground", TEXT_MAIN);
        UIManager.put("Component.focusColor", ACCENT);
        UIManager.put("Component.borderColor", BORDER_COLOR);
    }

    // ── Constructor ─────────────────────────────────────────────────────────
    public SwingGui() {
        super("Iterated Prisoner's Dilemma Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(960, 620));
        setPreferredSize(new Dimension(1600, 900));
        getContentPane().setBackground(SURFACE);

        setLayout(new BorderLayout(0, 0));
        add(buildHeader(), BorderLayout.NORTH);
        add(buildMainArea(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);   // centre on screen
    }

    // ── Header ──────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x141B27));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        // Left: logo placeholder + title
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        // Simple geometric "logo" drawn as a custom component
        left.add(new LogoIcon());

        JLabel title = new JLabel("Iterated Prisoner's Dilemma Simulator");
        title.setFont(new Font("Monospaced", Font.BOLD, 20));
        title.setForeground(ACCENT);
        left.add(title);

        JLabel subtitle = new JLabel("  —  Axelrod (1984)");
        subtitle.setFont(new Font("Monospaced", Font.PLAIN, 11));
        subtitle.setForeground(TEXT_MUTED);
        left.add(subtitle);

        header.add(left, BorderLayout.WEST);
        return header;
    }

    // ── Three-column main area ───────────────────────────────────────────────
    private JPanel buildMainArea() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(SURFACE);
        main.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 8);

        gbc.gridx = 0;
        gbc.weightx = 0.25;
        main.add(buildStrategiesPanel(), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.4;
        gbc.insets = new Insets(0, 0, 0, 8);
        main.add(buildTournamentPanel(), gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.35;
        gbc.insets = new Insets(0, 0, 0, 0);
        main.add(buildResultsPanel(), gbc);

        return main;
    }

    // ── Panel 1: Strategies ─────────────────────────────────────────────────
    private JPanel buildStrategiesPanel() {
        JPanel panel = createCard("STRATEGIES");

        // Checkboxes - only implemented strategies
        String[] strategyNames = StrategyRegistry.getStrategies()
                .stream()
                .map(Strategy::getName)
                .toArray(String[]::new);

        JPanel listPanel = new JPanel();
        listPanel.setOpaque(false);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        for (int i = 0; i < strategyNames.length; i++) {
            JCheckBox cb = new JCheckBox(strategyNames[i], true);
            cb.setOpaque(false);
            cb.setFont(new Font("Monospaced", Font.PLAIN, 12));
            cb.setForeground(TEXT_MAIN);
            cb.setFocusPainted(false);
            strategyCheckboxes.put(strategyNames[i], cb);
            listPanel.add(cb);
            listPanel.add(Box.createVerticalStrut(2));
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(SURFACE2);
        panel.add(scroll, BorderLayout.CENTER);

        // Buttons
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 6, 0));
        btnRow.setOpaque(false);
        btnRow.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        btnRow.add(accentButton("+ Add Custom"));
        btnRow.add(mutedButton("Remove"));
        panel.add(btnRow, BorderLayout.SOUTH);

        return panel;
    }

    // ── Panel 2: Tournament Setup ────────────────────────────────────────────
    private JPanel buildTournamentPanel() {
        JPanel panel = createCard("TOURNAMENT SETUP");
        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        // Rounds
        JPanel roundsRow = new JPanel(new BorderLayout(8, 0));
        roundsRow.setOpaque(false);
        roundsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        roundsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel roundsLabel = new JLabel("Rounds:");
        roundsLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        roundsLabel.setForeground(TEXT_MUTED);
        roundsLabel.setPreferredSize(new Dimension(70, 22));
        roundsRow.add(roundsLabel, BorderLayout.WEST);
        roundsField = new JTextField("200");
        roundsField.setFont(new Font("Monospaced", Font.PLAIN, 12));
        roundsField.setBackground(SURFACE);
        roundsField.setForeground(TEXT_MAIN);
        roundsField.setCaretColor(ACCENT);
        roundsField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        roundsRow.add(roundsField, BorderLayout.CENTER);
        inner.add(roundsRow);
        inner.add(Box.createVerticalStrut(10));

        // Noise
        JPanel noiseRow = new JPanel(new BorderLayout(8, 0));
        noiseRow.setOpaque(false);
        noiseRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        noiseRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel noiseLabel = new JLabel("Noise  :");
        noiseLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        noiseLabel.setForeground(TEXT_MUTED);
        noiseLabel.setPreferredSize(new Dimension(70, 22));
        noiseRow.add(noiseLabel, BorderLayout.WEST);
        noiseField = new JTextField("0.01");
        noiseField.setFont(new Font("Monospaced", Font.PLAIN, 12));
        noiseField.setBackground(SURFACE);
        noiseField.setForeground(TEXT_MAIN);
        noiseField.setCaretColor(ACCENT);
        noiseField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        noiseRow.add(noiseField, BorderLayout.CENTER);
        inner.add(noiseRow);
        inner.add(Box.createVerticalStrut(16));

        // Payoff matrix label
        JLabel matrixLabel = new JLabel("Payoff Matrix:");
        matrixLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        matrixLabel.setForeground(ACCENT);
        matrixLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(matrixLabel);
        inner.add(Box.createVerticalStrut(16));

        // R, T, P, S fields in a 2x2 grid
        JPanel matrix = new JPanel(new GridLayout(2, 2, 8, 6));
        matrix.setOpaque(false);
        matrix.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        matrix.setAlignmentX(Component.LEFT_ALIGNMENT);

        rewardField = createMatrixTextField("3");
        temptationField = createMatrixTextField("5");
        punishmentField = createMatrixTextField("1");
        suckerField = createMatrixTextField("0");

        JPanel rCell = new JPanel(new BorderLayout(0, 2));
        rCell.setOpaque(false);
        rCell.add(new JLabel("R (Reward)"), BorderLayout.NORTH);
        rCell.add(rewardField, BorderLayout.CENTER);

        JPanel tCell = new JPanel(new BorderLayout(0, 2));
        tCell.setOpaque(false);
        tCell.add(new JLabel("T (Temptation)"), BorderLayout.NORTH);
        tCell.add(temptationField, BorderLayout.CENTER);

        JPanel pCell = new JPanel(new BorderLayout(0, 2));
        pCell.setOpaque(false);
        pCell.add(new JLabel("P (Punishment)"), BorderLayout.NORTH);
        pCell.add(punishmentField, BorderLayout.CENTER);

        JPanel sCell = new JPanel(new BorderLayout(0, 2));
        sCell.setOpaque(false);
        sCell.add(new JLabel("S (Sucker)"), BorderLayout.NORTH);
        sCell.add(suckerField, BorderLayout.CENTER);

        matrix.add(rCell);
        matrix.add(tCell);
        matrix.add(pCell);
        matrix.add(sCell);
        inner.add(matrix);
        inner.add(Box.createVerticalStrut(20));

        // Mode selector
        JLabel modeLabel = new JLabel("Mode:");
        modeLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        modeLabel.setForeground(ACCENT);
        modeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(modeLabel);
        inner.add(Box.createVerticalStrut(6));

        String[] modes = {"Round Robin", "Ecological (Population)"};
        JComboBox<String> modeBox = new JComboBox<>(modes);
        modeBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        modeBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        modeBox.setBackground(SURFACE2);
        modeBox.setForeground(TEXT_MAIN);
        modeBox.setFont(new Font("Monospaced", Font.PLAIN, 12));
        inner.add(modeBox);

        panel.add(inner, BorderLayout.CENTER);

        // Run button
        JButton runBtn = new JButton("▶  Run Tournament");
        runBtn.setFont(new Font("Monospaced", Font.BOLD, 13));
        runBtn.setBackground(ACCENT);
        runBtn.setForeground(new Color(0x0A1520));
        runBtn.setFocusPainted(false);
        runBtn.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        runBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        runBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                runBtn.setBackground(ACCENT.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                runBtn.setBackground(ACCENT);
            }
        });
        runBtn.addActionListener(e -> onRunTournament());

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        south.add(runBtn, BorderLayout.CENTER);
        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    // ── Panel 3: Results ─────────────────────────────────────────────────────
    private JPanel buildResultsPanel() {
        JPanel panel = createCard("RESULTS");

        // Table
        String[] cols = {"#", "Strategy", "Score", "Coop %"};
        resultsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(resultsModel);
        table.setFont(new Font("Monospaced", Font.PLAIN, 16));
        table.setRowHeight(22);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setFont(new Font("Monospaced", Font.BOLD, 11));
        table.getTableHeader().setReorderingAllowed(false);

        // Right-align score column
        DefaultTableCellRenderer rightAlign = new DefaultTableCellRenderer();
        rightAlign.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(rightAlign);
        table.getColumnModel().getColumn(3).setCellRenderer(rightAlign);

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(45);
        table.getColumnModel().getColumn(0).setMaxWidth(45);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(50);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setMaxWidth(125);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        panel.add(scroll, BorderLayout.CENTER);

        // Chart placeholder + export button
        JPanel south = new JPanel(new GridLayout(2, 1, 0, 6));
        south.setOpaque(false);
        south.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JPanel chartPlaceholder = new JPanel(new BorderLayout());
        chartPlaceholder.setBackground(SURFACE2);
        chartPlaceholder.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        chartPlaceholder.setPreferredSize(new Dimension(0, 70));
        JLabel chartHint = new JLabel("[ Score Chart — run tournament to populate ]", SwingConstants.CENTER);
        chartHint.setFont(new Font("Monospaced", Font.ITALIC, 10));
        chartHint.setForeground(TEXT_MUTED);
        chartPlaceholder.add(chartHint, BorderLayout.CENTER);
        south.add(chartPlaceholder);

        JButton exportBtn = mutedButton("⬇  Export CSV");
        south.add(exportBtn);

        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    // ── Status bar ───────────────────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setBackground(new Color(0x141B27));
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));

        statusLabel = new JLabel("Status: Ready");
        statusLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        statusLabel.setForeground(TEXT_MUTED);
        bar.add(statusLabel, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        JLabel progLabel = new JLabel("Progress:");
        progLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        progLabel.setForeground(TEXT_MUTED);
        right.add(progLabel);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(false);
        progressBar.setPreferredSize(new Dimension(160, 12));
        progressBar.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        right.add(progressBar);

        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Creates a titled card panel with BorderLayout.
     */
    private JPanel createCard(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(SURFACE2);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel label = new JLabel(title);
        label.setFont(new Font("Monospaced", Font.BOLD, 12));
        label.setForeground(ACCENT);
        label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(0, 0, 8, 0)
        ));
        card.add(label, BorderLayout.NORTH);
        return card;
    }

    /**
     * Labelled text field row.
     */
    private JPanel labeledField(String labelText, String defaultVal) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(labelText + ":");
        lbl.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lbl.setForeground(TEXT_MUTED);
        lbl.setPreferredSize(new Dimension(70, 22));
        row.add(lbl, BorderLayout.WEST);

        JTextField tf = new JTextField(defaultVal);
        tf.setFont(new Font("Monospaced", Font.PLAIN, 12));
        tf.setBackground(SURFACE);
        tf.setForeground(TEXT_MAIN);
        tf.setCaretColor(ACCENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        row.add(tf, BorderLayout.CENTER);
        return row;
    }

    /**
     * Small matrix cell with label above and field below.
     */
    private JPanel matrixCell(String label, String val) {
        JPanel cell = new JPanel(new BorderLayout(0, 2));
        cell.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Monospaced", Font.PLAIN, 10));
        lbl.setForeground(TEXT_MUTED);
        cell.add(lbl, BorderLayout.NORTH);

        JTextField tf = new JTextField(val);
        tf.setFont(new Font("Monospaced", Font.BOLD, 13));
        tf.setHorizontalAlignment(SwingConstants.CENTER);
        tf.setBackground(SURFACE);
        tf.setForeground(ACCENT);
        tf.setCaretColor(ACCENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)
        ));
        cell.add(tf, BorderLayout.CENTER);
        return cell;
    }

    private JButton accentButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Monospaced", Font.BOLD, 11));
        btn.setBackground(ACCENT);
        btn.setForeground(new Color(0x0A1520));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JTextField createMatrixTextField(String defaultVal) {
        JTextField tf = new JTextField(defaultVal);
        tf.setFont(new Font("Monospaced", Font.BOLD, 13));
        tf.setHorizontalAlignment(SwingConstants.CENTER);
        tf.setBackground(SURFACE);
        tf.setForeground(ACCENT);
        tf.setCaretColor(ACCENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)
        ));
        return tf;
    }

    private JButton mutedButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Monospaced", Font.PLAIN, 11));
        btn.setBackground(SURFACE);
        btn.setForeground(TEXT_MUTED);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Populate results table with placeholder data.
     */
    private void populateDemoResults() {
        resultsModel.setRowCount(0);
        Object[][] demo = {
                {"1", "Tit For Tat", 487, "94%"},
                {"2", "Always Cooperate", 456, "100%"},
                {"3", "Pavlov", 423, "78%"},
                {"4", "Tit For Two Tats", 401, "89%"},
                {"5", "Grim Trigger", 388, "72%"},
                {"6", "Random", 312, "50%"},
                {"7", "Always Defect", 201, "0%"},
        };
        for (Object[] row : demo) resultsModel.addRow(row);
    }

    /**
     * Runs the tournament using the engine.
     */
    private void onRunTournament() {
        // Gather selected strategies
        List<Strategy> selectedStrategies = new ArrayList<>();

        strategyCheckboxes.forEach((name, checkBox) -> {
                    if (checkBox.isSelected()) {
                        selectedStrategies.add(StrategyRegistry.getStrategy(name));
                    }
                }
        );

        if (selectedStrategies.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select at least one strategy.",
                    "No Strategies Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get tournament settings
        int rounds;
        try {
            rounds = Integer.parseInt(roundsField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid number of rounds.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get payoff matrix values
        int r, t, p, s;
        try {
            r = Integer.parseInt(rewardField.getText());
            t = Integer.parseInt(temptationField.getText());
            p = Integer.parseInt(punishmentField.getText());
            s = Integer.parseInt(suckerField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid payoff matrix values.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        PayoffMatrix matrix = new PayoffMatrix(r, t, p, s);

        statusLabel.setText("Status: Running tournament...");
        statusLabel.setForeground(ACCENT);
        progressBar.setValue(0);

        SwingWorker<Tournament, Integer> worker = new SwingWorker<>() {
            @Override
            protected Tournament doInBackground() throws Exception {
                Tournament tournament = new Tournament(selectedStrategies, rounds);
                int totalMatches = selectedStrategies.size() * selectedStrategies.size();
                int completed = 0;

                tournament.run(matrix);

                completed = totalMatches;
                publish(100);

                return tournament;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                progressBar.setValue(chunks.getLast());
            }

            @Override
            protected void done() {
                try {
                    Tournament tournament = get();
                    progressBar.setValue(100);
                    statusLabel.setText("Status: Tournament complete — " +
                            selectedStrategies.size() + " strategies, " + rounds + " rounds");
                    statusLabel.setForeground(new Color(0x66BB6A));
                    populateResults(tournament);
                } catch (Exception e) {
                    statusLabel.setText("Status: Error running tournament");
                    statusLabel.setForeground(Color.RED);
                    JOptionPane.showMessageDialog(SwingGui.this,
                            "Error: " + e.getMessage(),
                            "Tournament Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    /**
     * Populates results table with tournament data.
     */
    private void populateResults(Tournament tournament) {
        resultsModel.setRowCount(0);
        List<Strategy> ranking = tournament.getRanking();

        for (int i = 0; i < ranking.size(); i++) {
            Strategy strategy = ranking.get(i);
            double score = tournament.getScore(strategy);
            double coopRate = tournament.getCoopRates().getOrDefault(strategy, 0.0);
            resultsModel.addRow(new Object[]{
                    String.valueOf(i + 1),
                    strategy.getName(),
                    String.format("%.1f", score),
                    String.format("%.0f%%", coopRate * 100)
            });
        }
    }

    // ── Inner: tiny geometric logo ───────────────────────────────────────────
    private static class LogoIcon extends JComponent {
        LogoIcon() {
            setPreferredSize(new Dimension(28, 28));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Two overlapping squares (C vs D metaphor)
            g2.setColor(ACCENT);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRect(2, 2, 14, 14);
            g2.setColor(new Color(0xFF6B6B));
            g2.drawRect(10, 10, 14, 14);
            g2.dispose();
        }
    }
}