package dev.ahwz.engine;

import dev.ahwz.model.MatchResult;
import dev.ahwz.model.PayoffMatrix;
import dev.ahwz.model.Strategy;
import dev.ahwz.strategies.AlwaysCooporate;
import dev.ahwz.strategies.AlwaysDefect;
import org.junit.jupiter.api.Test;

class MatchTest {

    @Test
    void play() {
        Strategy playerA = new AlwaysCooporate();
        Strategy playerB = new AlwaysDefect();
        PayoffMatrix matrix = new PayoffMatrix(3,5,1,0);

        Match match = new Match();
        MatchResult result = match.play(playerA, playerB, 10, matrix);
        System.out.println(result);
    }

}