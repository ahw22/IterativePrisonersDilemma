package dev.ahwz.engine;

import dev.ahwz.model.MatchResult;
import dev.ahwz.model.PayoffMatrix;
import dev.ahwz.model.Strategy;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Tournament {
    private final List<Strategy> strategies;
    private final Map<Strategy, Double> totalScores;
    private final List<MatchResult> matchResults;
    private final List<Strategy> ranking;
    private final int rounds;

    public Tournament(List<Strategy> strategies, int rounds) {
        this.strategies = strategies;
        this.rounds = rounds;
        totalScores = new HashMap<>();
        matchResults = new ArrayList<>();
        ranking = new ArrayList<>();
    }

    public void run(PayoffMatrix matrix) {
        for (Strategy sA : strategies) {
            for (Strategy sB : strategies) {
                Match match = new Match();
                MatchResult result = match.play(sA, sB, rounds, matrix);
                matchResults.add(result);
            }
        }
        calculateScores();

        List<Strategy> desc = totalScores.entrySet().stream()
                .sorted(Map.Entry.<Strategy, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();

        ranking.addAll(desc);
    }

    private void calculateScores() {
        Map<Strategy, List<Integer>> allScoresMap = new HashMap<>();
        for (Strategy s : strategies) {
            allScoresMap.put(s, new ArrayList<>());
        }

        matchResults.forEach(matchResult -> {
            List<Integer> scoresA = allScoresMap.get(matchResult.playerA());
            scoresA.add(matchResult.scoreA());

            List<Integer> scoresB = allScoresMap.get(matchResult.playerB());
            scoresB.add(matchResult.scoreB());
        });

        allScoresMap.forEach((strategy, scores) -> {
            double average = scores.stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0);
            totalScores.put(strategy, average);
        });


    }

    public double getScore(Strategy strategy) {
        return totalScores.get(strategy);
    }

    public List<MatchResult> getMatchesBetween(Strategy strategyA, Strategy strategyB) {
        return matchResults.stream()
                .filter(matchResult -> matchResult.getPlayers().contains(strategyA)
                        && matchResult.getPlayers().contains(strategyB))
                .toList();
    }


}
