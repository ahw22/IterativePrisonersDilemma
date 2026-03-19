package dev.ahwz.ipd.strategies;

import dev.ahwz.ipd.model.Action;
import dev.ahwz.ipd.model.GameHistory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlwaysDefectTest {

    @Test
    void getName_returnsCorrectName() {
        AlwaysDefect strategy = new AlwaysDefect();
        assertEquals("Always Defect", strategy.getName());
    }

    @Test
    void getAction_onEmptyHistory_returnsDefect() {
        AlwaysDefect strategy = new AlwaysDefect();
        GameHistory history = new GameHistory(100);
        assertEquals(Action.DEFECT, strategy.getAction(history));
    }

    @Test
    void getAction_afterMultipleRounds_alwaysReturnsDefect() {
        AlwaysDefect strategy = new AlwaysDefect();
        GameHistory history = new GameHistory(100);
        
        for (int i = 0; i < 10; i++) {
            history.recordMove(Action.DEFECT, Action.COOPERATE);
            assertEquals(Action.DEFECT, strategy.getAction(history));
        }
    }
}
