package dev.ahwz.ipd.engine;

import dev.ahwz.ipd.model.*;

import java.util.ArrayList;
import java.util.List;

public class Match {

    public MatchResult play(Strategy playerA, Strategy playerB, int rounds, PayoffMatrix payoffMatrix) {
        GameHistory historyA = new GameHistory(rounds);
        GameHistory historyB = new GameHistory(rounds);
        List<RoundResult> roundResults = new ArrayList<>();

        for (int i = 0; i < rounds; i++) {
            Action moveA = playerA.getAction(historyA);
            Action moveB = playerB.getAction(historyB);

            historyA.recordMove(moveA, moveB);
            historyB.recordMove(moveB, moveA);

            roundResults.add(new RoundResult(moveA, moveB, payoffMatrix.payoff(moveA,moveB), payoffMatrix.payoff(moveB,moveA)));
        }

        return new MatchResult(playerA, playerB, roundResults.stream().mapToInt(RoundResult::payoffA).sum(), roundResults.stream().mapToInt(
                RoundResult::payoffB).sum(), rounds, roundResults);
    }
}
