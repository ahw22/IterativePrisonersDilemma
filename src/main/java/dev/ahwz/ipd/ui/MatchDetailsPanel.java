package dev.ahwz.ipd.ui;

import dev.ahwz.ipd.engine.Tournament;
import dev.ahwz.ipd.model.Action;
import dev.ahwz.ipd.model.MatchResult;
import dev.ahwz.ipd.model.RoundResult;
import dev.ahwz.ipd.model.Strategy;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MatchDetailsPanel extends JPanel {

    private static final Color ACCENT = new Color(0x4FC3F7);
    private static final Color SURFACE = new Color(0x1E2533);
    private static final Color SURFACE2 = new Color(0x252D3D);
    private static final Color BORDER_COLOR = new Color(0x2E3A50);
    private static final Color TEXT_MUTED = new Color(0x8899AA);
    private static final Color TEXT_MAIN = new Color(0xDDEEFF);
    private static final Color COOPERATE_COLOR = new Color(0x66BB6A);
    private static final Color DEFECT_COLOR = new Color(0xEF5350);

    private final Tournament tournament;
    private Strategy selectedStrategy;
    private JComboBox<String> matchSelector;
    private JPanel timelinePanel;
    private JTable roundTable;
    private DefaultTableModel roundTableModel;
    private List<MatchResult> currentMatches;
    private JLabel summaryLabel;

    public MatchDetailsPanel(Tournament tournament) {
        super(new BorderLayout());
        this.tournament = tournament;
        this.currentMatches = new ArrayList<>();

        setBackground(SURFACE);
        setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        initComponents();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout(8, 0));
        topPanel.setBackground(SURFACE2);
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel matchLabel = new JLabel("Match:");
        matchLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        matchLabel.setForeground(ACCENT);
        topPanel.add(matchLabel, BorderLayout.WEST);

        matchSelector = new JComboBox<>();
        matchSelector.setFont(new Font("Monospaced", Font.PLAIN, 12));
        matchSelector.setBackground(SURFACE);
        matchSelector.setForeground(TEXT_MAIN);
        matchSelector.addActionListener(e -> onMatchSelected());
        topPanel.add(matchSelector, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 8));
        centerPanel.setBackground(SURFACE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        summaryLabel = new JLabel("Select a strategy to view match details");
        summaryLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        summaryLabel.setForeground(TEXT_MUTED);
        summaryLabel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        summaryLabel.setOpaque(true);
        summaryLabel.setBackground(SURFACE2);
        summaryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        summaryLabel.setPreferredSize(new Dimension(0, 40));
        centerPanel.add(summaryLabel, BorderLayout.NORTH);

        timelinePanel = new JPanel(new BorderLayout());
        timelinePanel.setBackground(SURFACE);
        timelinePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                "Visual Timeline",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Monospaced", Font.BOLD, 11),
                TEXT_MAIN
        ));
        centerPanel.add(timelinePanel, BorderLayout.CENTER);

        String[] roundCols = {"Round", "Player A", "Opponent B", "Noise", "Payoff A", "Payoff B", "Running A", "Running B"};
        roundTableModel = new DefaultTableModel(roundCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        roundTable = new JTable(roundTableModel);
        roundTable.setFont(new Font("Monospaced", Font.PLAIN, 11));
        roundTable.setRowHeight(22);
        roundTable.setBackground(SURFACE2);
        roundTable.setForeground(TEXT_MAIN);
        roundTable.setGridColor(BORDER_COLOR);
        roundTable.setSelectionBackground(ACCENT.darker());
        roundTable.setSelectionForeground(TEXT_MAIN);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < roundTable.getColumnCount(); i++) {
            roundTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        roundTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null && !value.toString().isEmpty()) {
                    setForeground(Color.YELLOW);
                    setFont(new Font("Monospaced", Font.BOLD, 11));
                } else {
                    setForeground(TEXT_MUTED);
                    setFont(new Font("Monospaced", Font.PLAIN, 11));
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        JScrollPane tableScroll = new JScrollPane(roundTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        tableScroll.setBackground(SURFACE);
        centerPanel.add(tableScroll, BorderLayout.SOUTH);
        tableScroll.setPreferredSize(new Dimension(0, 200));

        add(centerPanel, BorderLayout.CENTER);
    }

    public void setStrategy(Strategy strategy) {
        this.selectedStrategy = strategy;
        if (strategy == null) {
            matchSelector.removeAllItems();
            matchSelector.addItem("No strategy selected");
            summaryLabel.setText("Select a strategy to view match details");
            timelinePanel.removeAll();
            timelinePanel.repaint();
            roundTableModel.setRowCount(0);
            return;
        }

        currentMatches = tournament.getMatchesForStrategy(strategy);
        matchSelector.removeAllItems();

        if (currentMatches.isEmpty()) {
            matchSelector.addItem("No matches found");
            summaryLabel.setText("No matches found for " + strategy.getName());
            timelinePanel.removeAll();
            timelinePanel.repaint();
            roundTableModel.setRowCount(0);
            return;
        }

        for (MatchResult match : currentMatches) {
            String opponentName = match.playerA().equals(strategy) 
                    ? match.playerB().getName() 
                    : match.playerA().getName();
            String item = strategy.getName() + " vs " + opponentName;
            matchSelector.addItem(item);
        }

        onMatchSelected();
    }

    private void onMatchSelected() {
        int selectedIndex = matchSelector.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= currentMatches.size()) {
            return;
        }

        MatchResult match = currentMatches.get(selectedIndex);
        displayMatch(match);
    }

    private void displayMatch(MatchResult match) {
        Strategy playerA = match.playerA();
        Strategy playerB = match.playerB();

        double coopRateA = match.coopRateA() * 100;
        double coopRateB = match.coopRateB() * 100;

        summaryLabel.setText(String.format(
                "<html>Score: %s %d vs %s %d | Coop: %.0f%% vs %.0f%%</html>",
                playerA.getName(), match.scoreA(), playerB.getName(), match.scoreB(),
                coopRateA, coopRateB
        ));

        timelinePanel.removeAll();
        timelinePanel.setLayout(new BorderLayout(0, 8));

        List<RoundResult> rounds = match.roundResults();

        timelinePanel.add(createTimelineScrollPane(playerA, playerB, rounds, match), BorderLayout.CENTER);
        timelinePanel.revalidate();
        timelinePanel.repaint();
        updateRoundTable(playerA, playerB, rounds, match);
    }

    private JScrollPane createTimelineScrollPane(Strategy playerA, Strategy playerB, List<RoundResult> rounds, MatchResult match) {
        JPanel timeline = new JPanel(new GridBagLayout());
        timeline.setBackground(SURFACE);
        GridBagConstraints gbc = createGridBagConstraints();

        timeline.add(createPlayerLabel(playerA.getName(), ACCENT), gbc(0, 0, 0));
        timeline.add(createPlayerActionRow(playerA, rounds, match), gbc(1, 0, 1));

        timeline.add(createPlayerLabel(playerB.getName(), COOPERATE_COLOR), gbc(0, 1, 0));
        timeline.add(createPlayerActionRow(playerB, rounds, match), gbc(1, 1, 1));

        timeline.add(createRoundNumberRow(rounds.size()), gbc(1, 2, 1));
        timeline.add(createLegend(), gbc(0, 3, 2, 1, 2));

        JScrollPane scrollPane = new JScrollPane(timeline);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBackground(SURFACE);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JPanel createPlayerActionRow(Strategy player, List<RoundResult> rounds, MatchResult match) {
        boolean isPlayerA = player.equals(match.playerA());
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 0));
        row.setBackground(SURFACE);

        for (RoundResult rr : rounds) {
            Action action = isPlayerA ? rr.moveA() : rr.moveB();
            boolean isNoisy = isPlayerA ? rr.noisyA() : rr.noisyB();
            row.add(new JBlock(action == Action.COOPERATE, isNoisy));
        }
        return row;
    }

    private void updateRoundTable(Strategy playerA, Strategy playerB, List<RoundResult> rounds, MatchResult match) {
        roundTableModel.setRowCount(0);
        int runningScoreA = 0;
        int runningScoreB = 0;

        for (int i = 0; i < rounds.size(); i++) {
            RoundResult rr = rounds.get(i);
            runningScoreA += rr.payoffA();
            runningScoreB += rr.payoffB();

            Action actionA = playerA.equals(match.playerA()) ? rr.moveA() : rr.moveB();
            Action actionB = playerB.equals(match.playerB()) ? rr.moveB() : rr.moveA();
            boolean noisyA = playerA.equals(match.playerA()) ? rr.noisyA() : rr.noisyB();
            boolean noisyB = playerB.equals(match.playerB()) ? rr.noisyB() : rr.noisyA();

            String noiseIndicator = buildNoiseIndicator(noisyA, noisyB);

            roundTableModel.addRow(new Object[]{
                    i + 1,
                    actionA.name().charAt(0),
                    actionB.name().charAt(0),
                    noiseIndicator,
                    rr.payoffA(),
                    rr.payoffB(),
                    runningScoreA,
                    runningScoreB
            });
        }
    }

    private String buildNoiseIndicator(boolean noisyA, boolean noisyB) {
        if (noisyA && noisyB) return "⚠ A, B";
        if (noisyA) return "⚠ A";
        if (noisyB) return "⚠ B";
        return "";
    }

    private GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        return gbc;
    }

    private GridBagConstraints gbc(int gridx, int gridy, int weightx) {
        GridBagConstraints gbc = createGridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.weightx = weightx;
        return gbc;
    }

    private GridBagConstraints gbc(int gridx, int gridy, int weightx, int gridwidth, int gridheight) {
        GridBagConstraints gbc = gbc(gridx, gridy, weightx);
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        return gbc;
    }

    private JPanel createPlayerLabel(String name, Color color) {
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        labelPanel.setBackground(SURFACE);
        JLabel label = new JLabel(name);
        label.setFont(new Font("Monospaced", Font.BOLD, 10));
        label.setForeground(color);
        labelPanel.add(label);
        return labelPanel;
    }

    private JPanel createRoundNumberRow(int count) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 0));
        row.setBackground(SURFACE);
        for (int i = 0; i < count; i++) {
            JLabel num = new JLabel(String.valueOf(i + 1));
            num.setFont(new Font("Monospaced", Font.PLAIN, 8));
            num.setForeground(TEXT_MUTED);
            num.setPreferredSize(new Dimension(18, 15));
            row.add(num);
        }
        return row;
    }

    private JPanel createLegend() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        legend.setBackground(SURFACE);
        legend.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        legend.add(createLegendLabel("C = Cooperate", COOPERATE_COLOR));
        legend.add(createLegendLabel("D = Defect", DEFECT_COLOR));
        legend.add(createLegendLabel("⚠ = Noise", TEXT_MUTED));
        return legend;
    }

    private JLabel createLegendLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Monospaced", Font.PLAIN, 10));
        label.setForeground(color);
        return label;
    }

    private static class JBlock extends JPanel {
        JBlock(boolean isCooperate, boolean isNoisy) {
            setPreferredSize(new Dimension(18, 28));
            setBackground(isCooperate ? COOPERATE_COLOR : DEFECT_COLOR);
            setBorder(BorderFactory.createLineBorder(
                    isNoisy ? Color.YELLOW : Color.BLACK, 
                    isNoisy ? 2 : 1
            ));
            
            JLabel label = new JLabel(isCooperate ? "C" : "D");
            label.setFont(new Font("Monospaced", Font.BOLD, 10));
            label.setForeground(Color.WHITE);
            add(label);
        }
    }
}
