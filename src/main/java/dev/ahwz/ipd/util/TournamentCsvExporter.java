package dev.ahwz.ipd.util;

import dev.ahwz.ipd.engine.Tournament;
import dev.ahwz.ipd.model.MatchResult;
import dev.ahwz.ipd.model.RoundResult;
import dev.ahwz.ipd.model.Strategy;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TournamentCsvExporter {

    private static final Path OUTPUT_FOLDER = Path.of("outputData");
    private static final Path MATCH_FOLDER = OUTPUT_FOLDER.resolve("matches");

    public static void exportTournament(Tournament tournament) throws IOException {

        Files.createDirectories(OUTPUT_FOLDER);
        Files.createDirectories(MATCH_FOLDER);

        exportAverageScores(tournament);
        exportMatches(tournament);
        exportIndividualMatches(tournament);
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

            writer.write("PlayerA,PlayerB,ScoreA,ScoreB,Rounds");
            writer.newLine();

            for (MatchResult result : tournament.getMatchResults()) {

                writer.write(
                        result.playerA().getName() + "," +
                                result.playerB().getName() + "," +
                                result.scoreA() + "," +
                                result.scoreB() + "," +
                                result.rounds()
                );

                writer.newLine();
            }
        }
    }

    private static void exportIndividualMatches(Tournament tournament) throws IOException {

        int matchIndex = 1;

        for (MatchResult match : tournament.getMatchResults()) {

            String fileName =
                    match.playerA().getName() + "_" +
                            match.playerB().getName() + "_" +
                            matchIndex + ".csv";

            Path file = MATCH_FOLDER.resolve(fileName);

            try (BufferedWriter writer = Files.newBufferedWriter(file)) {

                writer.write("Round,MoveA,MoveB,PayoffA,PayoffB");
                writer.newLine();

                int round = 1;

                for (RoundResult roundResult : match.roundResults()) {

                    writer.write(
                            round + "," +
                                    roundResult.moveA() + "," +
                                    roundResult.moveB() + "," +
                                    roundResult.payoffA() + "," +
                                    roundResult.payoffB()
                    );

                    writer.newLine();
                    round++;
                }
                writer.write("SUM,,," + match.scoreA() + "," + match.scoreB());
            }

            matchIndex++;
        }
    }
}