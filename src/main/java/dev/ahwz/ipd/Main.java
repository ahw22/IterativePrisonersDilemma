package dev.ahwz.ipd;

import dev.ahwz.ipd.engine.Tournament;
import dev.ahwz.ipd.model.PayoffMatrix;
import dev.ahwz.ipd.model.Strategy;
import dev.ahwz.ipd.registry.StrategyRegistry;
import dev.ahwz.ipd.util.TournamentCsvExporter;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Strategy> strategies = StrategyRegistry.getStrategies();
        PayoffMatrix matrix = new PayoffMatrix(3, 5, 1, 0);
        Tournament tournament = new Tournament(strategies, 200);

        tournament.run(matrix);

        System.out.println("Strategy     | Scores");
        tournament.getRanking().forEach(strategy -> {
            Double score = tournament.getTotalScores().get(strategy);
            System.out.println(strategy.getName() + ": " + score);
        });

        try {
        TournamentCsvExporter.exportTournament(tournament);
        } catch (Exception e) {
            System.out.println("Could not export data. " + e.getMessage());
        }

    }
}
