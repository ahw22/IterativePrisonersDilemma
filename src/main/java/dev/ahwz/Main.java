package dev.ahwz;

import dev.ahwz.engine.Tournament;
import dev.ahwz.model.PayoffMatrix;
import dev.ahwz.model.Strategy;
import dev.ahwz.strategies.AlwaysCooporate;
import dev.ahwz.strategies.AlwaysDefect;
import dev.ahwz.strategies.RandomStrategy;
import dev.ahwz.strategies.TitForTat;
import dev.ahwz.util.TournamentCsvExporter;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Strategy> strategies = List.of(
                new AlwaysCooporate(),
                new AlwaysDefect(),
                new TitForTat(),
                new RandomStrategy()
        );
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
