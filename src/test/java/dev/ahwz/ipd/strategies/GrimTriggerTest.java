package dev.ahwz.ipd.strategies;

import dev.ahwz.ipd.model.Action;
import dev.ahwz.ipd.model.GameHistory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GrimTriggerTest {

    @Test
    void getName_returnsCorrectName() {
        GrimTrigger strategy = new GrimTrigger();
        assertEquals("Grim Trigger", strategy.getName());
    }

    @Test
    void getAction_onFirstMove_returnsCooperate() {
        GrimTrigger strategy = new GrimTrigger();
        GameHistory history = new GameHistory(100);
        assertEquals(Action.COOPERATE, strategy.getAction(history));
    }

    @Test
    void getAction_afterOpponentCooperated_keepsCooperating() {
        GrimTrigger strategy = new GrimTrigger();
        GameHistory history = new GameHistory(100);
        
        for (int i = 0; i < 5; i++) {
            history.recordMove(Action.COOPERATE, Action.COOPERATE);
            assertEquals(Action.COOPERATE, strategy.getAction(history));
        }
    }

    @Test
    void getAction_onceOpponentDefected_alwaysDefects() {
        GrimTrigger strategy = new GrimTrigger();
        GameHistory history = new GameHistory(100);
        
        history.recordMove(Action.COOPERATE, Action.COOPERATE);
        assertEquals(Action.COOPERATE, strategy.getAction(history));
        
        history.recordMove(Action.COOPERATE, Action.DEFECT);
        assertEquals(Action.DEFECT, strategy.getAction(history));
        
        for (int i = 0; i < 5; i++) {
            history.recordMove(Action.DEFECT, Action.COOPERATE);
            assertEquals(Action.DEFECT, strategy.getAction(history));
        }
    }
}
