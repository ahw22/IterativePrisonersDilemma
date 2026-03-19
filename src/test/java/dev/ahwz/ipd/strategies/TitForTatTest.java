package dev.ahwz.ipd.strategies;

import dev.ahwz.ipd.model.Action;
import dev.ahwz.ipd.model.GameHistory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TitForTatTest {

    @Test
    void getName_returnsCorrectName() {
        TitForTat strategy = new TitForTat();
        assertEquals("Tit for Tat", strategy.getName());
    }

    @Test
    void getAction_onFirstMove_returnsCooperate() {
        TitForTat strategy = new TitForTat();
        GameHistory history = new GameHistory(100);
        assertEquals(Action.COOPERATE, strategy.getAction(history));
    }

    @Test
    void getAction_afterOpponentCooperated_returnsCooperate() {
        TitForTat strategy = new TitForTat();
        GameHistory history = new GameHistory(100);
        history.recordMove(Action.COOPERATE, Action.COOPERATE);
        
        assertEquals(Action.COOPERATE, strategy.getAction(history));
    }

    @Test
    void getAction_afterOpponentDefected_returnsDefect() {
        TitForTat strategy = new TitForTat();
        GameHistory history = new GameHistory(100);
        history.recordMove(Action.COOPERATE, Action.DEFECT);
        
        assertEquals(Action.DEFECT, strategy.getAction(history));
    }

    @Test
    void getAction_respondsToLastMove_only() {
        TitForTat strategy = new TitForTat();
        GameHistory history = new GameHistory(100);
        
        history.recordMove(Action.COOPERATE, Action.DEFECT);
        assertEquals(Action.DEFECT, strategy.getAction(history));
        
        history.recordMove(Action.DEFECT, Action.COOPERATE);
        assertEquals(Action.COOPERATE, strategy.getAction(history));
    }
}
