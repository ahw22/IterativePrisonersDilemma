package dev.ahwz.ipd.ui;

import dev.ahwz.ipd.engine.Tournament;
import dev.ahwz.ipd.model.Strategy;

import javax.swing.*;
import java.awt.*;

public class DataViewWindow extends JFrame {

    private static final Color ACCENT = new Color(0x4FC3F7);
    private static final Color SURFACE = new Color(0x1E2533);
    private static final Color SURFACE2 = new Color(0x252D3D);
    private static final Color BORDER_COLOR = new Color(0x2E3A50);
    private static final Color TEXT_MAIN = new Color(0xDDEEFF);

    private final Tournament tournament;
    private final ScoreChartPanel scoreChartPanel;
    private final MatchDetailsPanel matchDetailsPanel;
    private JTabbedPane tabbedPane;

    public DataViewWindow(Tournament tournament) {
        super("Data View - Tournament Results");
        this.tournament = tournament;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 700));
        setPreferredSize(new Dimension(1200, 800));
        getContentPane().setBackground(SURFACE);

        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x141B27));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        JLabel title = new JLabel("Tournament Data View");
        title.setFont(new Font("Monospaced", Font.BOLD, 18));
        title.setForeground(ACCENT);
        header.add(title, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Monospaced", Font.BOLD, 12));
        tabbedPane.setBackground(SURFACE2);
        tabbedPane.setForeground(TEXT_MAIN);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        scoreChartPanel = new ScoreChartPanel(tournament, this::onStrategySelected);
        matchDetailsPanel = new MatchDetailsPanel(tournament);

        tabbedPane.addTab("Strategy Scores", scoreChartPanel);
        tabbedPane.addTab("Match Details", matchDetailsPanel);

        tabbedPane.setBackgroundAt(0, SURFACE2);
        tabbedPane.setBackgroundAt(1, SURFACE2);
        tabbedPane.setForegroundAt(0, TEXT_MAIN);
        tabbedPane.setForegroundAt(1, TEXT_MAIN);

        add(tabbedPane, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private void onStrategySelected(Strategy strategy) {
        tabbedPane.setSelectedIndex(1);
        matchDetailsPanel.setStrategy(strategy);
    }

    public void refresh() {
        scoreChartPanel.refresh();
    }
}
