package dev.ahwz.ipd.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PayoffMatrixTest {

    private final PayoffMatrix matrix = new PayoffMatrix(3, 5, 1, 0);

    @Test
    void bothCooperate_returnsReward() {
        assertEquals(3, matrix.payoff(Action.COOPERATE, Action.COOPERATE));
    }

    @Test
    void bothDefect_returnsPunishment() {
        assertEquals(1, matrix.payoff(Action.DEFECT, Action.DEFECT));
    }

    @Test
    void cooperateAgainstDefect_returnsSucker() {
        assertEquals(0, matrix.payoff(Action.COOPERATE, Action.DEFECT));
    }

    @Test
    void defectAgainstCooperate_returnsTemptation() {
        assertEquals(5, matrix.payoff(Action.DEFECT, Action.COOPERATE));
    }

    @Test
    void recordGetters_returnCorrectValues() {
        assertEquals(3, matrix.reward());
        assertEquals(5, matrix.temptation());
        assertEquals(1, matrix.punishment());
        assertEquals(0, matrix.sucker());
    }
}
