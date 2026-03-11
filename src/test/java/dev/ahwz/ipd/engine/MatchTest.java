package dev.ahwz.ipd.engine;

import dev.ahwz.ipd.model.MatchResult;
import dev.ahwz.ipd.model.PayoffMatrix;
import dev.ahwz.ipd.model.Strategy;
import dev.ahwz.ipd.strategies.AlwaysCooporate;
import dev.ahwz.ipd.strategies.AlwaysDefect;
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