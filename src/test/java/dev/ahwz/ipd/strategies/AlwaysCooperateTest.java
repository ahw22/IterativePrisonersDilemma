package dev.ahwz.ipd.strategies;

import dev.ahwz.ipd.model.Action;
import dev.ahwz.ipd.model.GameHistory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlwaysCooperateTest {

    @Test
    void getName_returnsCorrectName() {
        AlwaysCooperate strategy = new AlwaysCooperate();
        assertEquals("Always Cooperate", strategy.getName());
    }

    @Test
    void getAction_onEmptyHistory_returnsCooperate() {
        AlwaysCooperate strategy = new AlwaysCooperate();
        GameHistory history = new GameHistory(100);
        assertEquals(Action.COOPERATE, strategy.getAction(history));
    }

    @Test
    void getAction_afterMultipleRounds_alwaysReturnsCooperate() {
        AlwaysCooperate strategy = new AlwaysCooperate();
        GameHistory history = new GameHistory(100);
        
        for (int i = 0; i < 10; i++) {
            history.recordMove(Action.COOPERATE, Action.DEFECT);
            assertEquals(Action.COOPERATE, strategy.getAction(history));
        }
    }
}
