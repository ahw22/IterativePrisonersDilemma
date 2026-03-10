package dev.ahwz.util;

import dev.ahwz.engine.Tournament;
import dev.ahwz.model.MatchResult;
import dev.ahwz.model.Strategy;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TournamentCsvExporter {

    private static final Path OUTPUT_FOLDER = Path.of("outputData");

    public static void exportTournament(Tournament tournament) throws IOException {

        Files.createDirectories(OUTPUT_FOLDER);

        exportAverageScores(tournament);
        exportMatches(tournament);
    }

    private static void exportAverageScores(Tournament tournament) throws IOException {

        Path file = OUTPUT_FOLDER.resolve("average_scores.csv");

        try (BufferedWriter writer = Files.newBufferedWriter(file)) {

            writer.write("Rank,Strategy,AverageScore");
            writer.newLine();

            int rank = 1;

            for (Strategy strategy : tournament.getRanking()) {

                double score = tournament.getScore(strategy);

                writer.write(rank + "," + strategy.getName() + "," + score);
                writer.newLine();

                rank++;
            }
        }
    }

    private static void exportMatches(Tournament tournament) throws IOException {

        Path file = OUTPUT_FOLDER.resolve("matches.csv");

        try (BufferedWriter writer = Files.newBufferedWriter(file)) {

            writer.write("PlayerA,PlayerB,ScoreA,ScoreB");
            writer.newLine();

            for (MatchResult result : tournament.getMatchResults()) {

                writer.write(
                        result.playerA().getName() + "," +
                                result.playerB().getName() + "," +
                                result.scoreA() + "," +
                                result.scoreB()
                );

                writer.newLine();
            }
        }
    }
}