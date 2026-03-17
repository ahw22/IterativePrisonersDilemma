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

    public static void exportTournament(Tournament tournament, Path outputDir) throws IOException {
        Files.createDirectories(outputDir);
        Path matchFolder = outputDir.resolve("matches");
        Files.createDirectories(matchFolder);

        exportAverageScores(tournament, outputDir);
        exportMatches(tournament, outputDir);
        exportIndividualMatches(tournament, matchFolder);
    }

    public static void exportTournament(Tournament tournament) throws IOException {
        exportTournament(tournament, OUTPUT_FOLDER);
    }

    private static void exportAverageScores(Tournament tournament, Path outputFolder) throws IOException {

        Path file = outputFolder.resolve("average_scores.csv");

        try (BufferedWriter writer = Files.newBufferedWriter(file)) {

            writer.write("Rank,Strategy,AverageScore,Cooperation rates %");
            writer.newLine();

            int rank = 1;

            for (Strategy strategy : tournament.getRanking()) {

                double score = tournament.getScore(strategy);

                writer.write(rank + "," + strategy.getName() + "," + score + "," + tournament.getCoopRate(strategy));
                writer.newLine();

                rank++;
            }
        }
    }

    private static void exportMatches(Tournament tournament, Path outputFolder) throws IOException {

        Path file = outputFolder.resolve("matches.csv");

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

    private static void exportIndividualMatches(Tournament tournament, Path matchFolder) throws IOException {

        for (MatchResult match : tournament.getMatchResults()) {

            String fileName =
                    match.playerA().getName() + "_" +
                            match.playerB().getName() + ".csv";

            Path file = matchFolder.resolve(fileName);

            try (BufferedWriter writer = Files.newBufferedWriter(file)) {

                writer.write("Round,"+ match.playerA().getName() + "," + match.playerB().getName() + ",PayoffA,PayoffB,NoiseA,NoiseB");
                writer.newLine();

                int round = 1;

                for (RoundResult roundResult : match.roundResults()) {

                    writer.write(
                            round + "," +
                                    roundResult.moveA() + "," +
                                    roundResult.moveB() + "," +
                                    roundResult.payoffA() + "," +
                                    roundResult.payoffB() + "," +
                                    roundResult.noisyA() + "," +
                                    roundResult.noisyB()
                    );

                    writer.newLine();
                    round++;
                }
                writer.write("SUM,,," + match.scoreA() + "," + match.scoreB());
                writer.newLine();
                writer.write("CoopRates:," + match.coopRateA() + "," + match.coopRateB());
            }
        }
    }
}