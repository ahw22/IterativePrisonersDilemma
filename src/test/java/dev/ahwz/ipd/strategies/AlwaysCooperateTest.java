package dev.ahwz.ipd.strategies;

import dev.ahwz.ipd.model.Action;
import dev.ahwz.ipd.model.GameHistory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlwaysCooporateTest {

    @Test
    void getName_returnsCorrectName() {
        AlwaysCooporate strategy = new AlwaysCooporate();
        assertEquals("Always Cooperate", strategy.getName());
    }

    @Test
    void getAction_onEmptyHistory_returnsCooperate() {
        AlwaysCooporate strategy = new AlwaysCooporate();
        GameHistory history = new GameHistory(100);
        assertEquals(Action.COOPERATE, strategy.getAction(history));
    }

    @Test
    void getAction_afterMultipleRounds_alwaysReturnsCooperate() {
        AlwaysCooporate strategy = new AlwaysCooporate();
        GameHistory history = new GameHistory(100);
        
        for (int i = 0; i < 10; i++) {
            history.recordMove(Action.COOPERATE, Action.DEFECT);
            assertEquals(Action.COOPERATE, strategy.getAction(history));
        }
    }
}
