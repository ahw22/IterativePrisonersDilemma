package dev.ahwz.ipd.strategies;

import dev.ahwz.ipd.model.Action;
import dev.ahwz.ipd.model.GameHistory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SuspiciousTitForTatTest {

    @Test
    void getName_returnsCorrectName() {
        SuspiciousTitForTat strategy = new SuspiciousTitForTat();
        assertEquals("Suspicious Tit For Tat", strategy.getName());
    }

    @Test
    void getAction_onFirstMove_returnsDefect() {
        SuspiciousTitForTat strategy = new SuspiciousTitForTat();
        GameHistory history = new GameHistory(100);
        assertEquals(Action.DEFECT, strategy.getAction(history));
    }

    @Test
    void getAction_afterOpponentCooperated_returnsCooperate() {
        SuspiciousTitForTat strategy = new SuspiciousTitForTat();
        GameHistory history = new GameHistory(100);
        history.recordMove(Action.DEFECT, Action.COOPERATE);
        
        assertEquals(Action.COOPERATE, strategy.getAction(history));
    }

    @Test
    void getAction_afterOpponentDefected_returnsDefect() {
        SuspiciousTitForTat strategy = new SuspiciousTitForTat();
        GameHistory history = new GameHistory(100);
        history.recordMove(Action.DEFECT, Action.DEFECT);
        
        assertEquals(Action.DEFECT, strategy.getAction(history));
    }
}
