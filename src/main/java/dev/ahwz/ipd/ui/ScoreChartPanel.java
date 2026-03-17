package dev.ahwz.ipd.ui;

import dev.ahwz.ipd.engine.Tournament;
import dev.ahwz.ipd.model.Strategy;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class ScoreChartPanel extends JPanel {

    private static final Color ACCENT = new Color(0x4FC3F7);
    private static final Color SURFACE = new Color(0x1E2533);
    private static final Color BORDER_COLOR = new Color(0x2E3A50);
    private static final Color TEXT_MUTED = new Color(0x8899AA);
    private static final Color TEXT_MAIN = new Color(0xDDEEFF);
    private static final Color COOPERATE_COLOR = new Color(0x66BB6A);

    private final Tournament tournament;
    private final Consumer<Strategy> onStrategySelected;
    private ChartPanel chartPanel;
    private List<Strategy> ranking;

    public ScoreChartPanel(Tournament tournament, Consumer<Strategy> onStrategySelected) {
        super(new BorderLayout());
        this.tournament = tournament;
        this.onStrategySelected = onStrategySelected;
        this.ranking = tournament.getRanking();
        
        setBackground(SURFACE);
        setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        
        initChart();
    }

    private void initChart() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(SURFACE);
        
        DefaultCategoryDataset dataset = createCombinedDataset();

        JFreeChart chart = ChartFactory.createBarChart(
                "Strategy Scores & Cooperation Rates",
                "Strategy",
                "Value",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        chart.setBackgroundPaint(SURFACE);
        chart.getTitle().setPaint(TEXT_MAIN);
        chart.getTitle().setFont(new Font("Monospaced", Font.BOLD, 14));

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(SURFACE);
        plot.setDomainGridlinePaint(BORDER_COLOR);
        plot.setRangeGridlinePaint(BORDER_COLOR);
        plot.setOutlineVisible(false);
        plot.setDomainAxis(new CategoryAxis());

        plot.getDomainAxis().setLabelPaint(TEXT_MAIN);
        plot.getDomainAxis().setTickLabelPaint(TEXT_MUTED);
        plot.getDomainAxis().setTickLabelFont(new Font("Monospaced", Font.PLAIN, 10));

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLabelPaint(TEXT_MAIN);
        rangeAxis.setTickLabelPaint(TEXT_MUTED);
        rangeAxis.setTickLabelFont(new Font("Monospaced", Font.PLAIN, 10));
        rangeAxis.setRange(0, getMaxValue() * 1.15);

        BarRenderer renderer = new BarRenderer() {
            @Override
            public Paint getItemPaint(int row, int column) {
                if (row == 0) {
                    return ACCENT;
                } else {
                    return COOPERATE_COLOR;
                }
            }
        };
        
        renderer.setItemMargin(0.05);
        plot.setRenderer(renderer);

        chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(SURFACE);
        chartPanel.setPreferredSize(new Dimension(800, 350));
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);

        mainPanel.add(chartPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        buttonPanel.setBackground(SURFACE);
        buttonPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                "Click a strategy to view match details",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Monospaced", Font.PLAIN, 11),
                TEXT_MUTED
        ));

        for (Strategy strategy : ranking) {
            JButton btn = new JButton(strategy.getName());
            btn.setFont(new Font("Monospaced", Font.PLAIN, 10));
            btn.setBackground(SURFACE);
            btn.setForeground(ACCENT);
            btn.setBorder(BorderFactory.createLineBorder(ACCENT));
            btn.setFocusPainted(false);
            btn.addActionListener(e -> onStrategySelected.accept(strategy));
            buttonPanel.add(btn);
        }

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }

    private DefaultCategoryDataset createCombinedDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        ranking = tournament.getRanking();
        
        for (Strategy strategy : ranking) {
            double score = tournament.getScore(strategy);
            double coopRate = tournament.getCoopRate(strategy) * 100;
            
            dataset.addValue(score, "Score", strategy.getName());
            dataset.addValue(coopRate, "Coop %", strategy.getName());
        }
        return dataset;
    }

    private double getMaxValue() {
        double max = 0;
        for (Strategy s : ranking) {
            double score = tournament.getScore(s);
            double coop = tournament.getCoopRate(s) * 100;
            if (score > max) max = score;
            if (coop > max) max = coop;
        }
        return max;
    }

    public void refresh() {
        removeAll();
        ranking = tournament.getRanking();
        initChart();
        revalidate();
        repaint();
    }
}
