package dev.ahwz.ipd.engine;

import dev.ahwz.ipd.model.MatchResult;
import dev.ahwz.ipd.model.PayoffMatrix;
import dev.ahwz.ipd.model.Strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tournament {
    private final List<Strategy> strategies;
    private final Map<Strategy, Double> totalScores;
    private final Map<Strategy, Double> coopRates;
    private final List<MatchResult> matchResults;
    private final List<Strategy> ranking;
    private final int rounds;
    private final double noise;

    public Tournament(List<Strategy> strategies, int rounds, double noise) {
        this.strategies = strategies;
        this.rounds = rounds;
        this.noise = noise;
        totalScores = new HashMap<>();
        coopRates = new HashMap<>();
        matchResults = new ArrayList<>();
        ranking = new ArrayList<>();
    }

    public void run(PayoffMatrix matrix) {
        for (Strategy sA : strategies) {
            for (Strategy sB : strategies) {
                Match match = new Match();
                MatchResult result = match.play(sA, sB, rounds, matrix, noise);
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
        Map<Strategy, List<Double>> allRatesMap = new HashMap<>();

        for (Strategy s : strategies) {
            allScoresMap.put(s, new ArrayList<>());
            allRatesMap.put(s, new ArrayList<>());
        }

        matchResults.forEach(matchResult -> {
            List<Integer> scoresA = allScoresMap.get(matchResult.playerA());
            scoresA.add(matchResult.scoreA());
            List<Double> coopRatesA = allRatesMap.get(matchResult.playerA());
            coopRatesA.add(matchResult.coopRateA());

            List<Integer> scoresB = allScoresMap.get(matchResult.playerB());
            scoresB.add(matchResult.scoreB());
            List<Double> coopRatesB = allRatesMap.get(matchResult.playerB());
            coopRatesB.add(matchResult.coopRateB());
        });

        allScoresMap.forEach((strategy, scores) -> {
            double average = scores.stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0);
            totalScores.put(strategy, average);
        });

        allRatesMap.forEach((strategy, rates) -> {
            double average = rates.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0);
            coopRates.put(strategy, average);
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

    public double getCoopRate(Strategy strategy) {
        return coopRates.get(strategy);
    }

    public List<MatchResult> getMatchesForStrategy(Strategy strategy) {
        return matchResults.stream()
                .filter(mr -> mr.playerA().equals(strategy) || mr.playerB().equals(strategy))
                .toList();
    }

    public List<Strategy> getStrategies() {
        return this.strategies;
    }

    public Map<Strategy, Double> getTotalScores() {
        return this.totalScores;
    }

    public Map<Strategy, Double> getCoopRates() {
        return this.coopRates;
    }

    public List<MatchResult> getMatchResults() {
        return this.matchResults;
    }

    public List<Strategy> getRanking() {
        return this.ranking;
    }

    public int getRounds() {
        return this.rounds;
    }

    public double getNoise() {
        return this.noise;
    }
}
