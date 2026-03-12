package dev.ahwz.ipd.model;

import java.util.List;

public record MatchResult(Strategy playerA, Strategy playerB, int scoreA, int scoreB, int rounds, List<RoundResult> roundResults, double coopRateA, double coopRateB) {
    public List<Strategy> getPlayers() {
        return List.of(playerA, playerB);
    }
}

