package dev.ahwz.ipd.strategies;

import dev.ahwz.ipd.model.Action;
import dev.ahwz.ipd.model.GameHistory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PavlovTest {

    @Test
    void getName_returnsCorrectName() {
        Pavlov strategy = new Pavlov();
        assertEquals("Pavlov", strategy.getName());
    }

    @Test
    void getAction_onFirstMove_returnsCooperate() {
        Pavlov strategy = new Pavlov();
        GameHistory history = new GameHistory(100);
        assertEquals(Action.COOPERATE, strategy.getAction(history));
    }

    @Test
    void getAction_afterMutualCooperation_cooperates() {
        Pavlov strategy = new Pavlov();
        GameHistory history = new GameHistory(100);
        history.recordMove(Action.COOPERATE, Action.COOPERATE);
        
        assertEquals(Action.COOPERATE, strategy.getAction(history));
    }

    @Test
    void getAction_afterBeingExploited_defects() {
        Pavlov strategy = new Pavlov();
        GameHistory history = new GameHistory(100);
        history.recordMove(Action.COOPERATE, Action.DEFECT);
        
        assertEquals(Action.DEFECT, strategy.getAction(history));
    }

    @Test
    void getAction_afterSuccessfulDefection_cooperates() {
        Pavlov strategy = new Pavlov();
        GameHistory history = new GameHistory(100);
        history.recordMove(Action.DEFECT, Action.COOPERATE);
        
        assertEquals(Action.COOPERATE, strategy.getAction(history));
    }

    @Test
    void getAction_afterMutualDefection_defects() {
        Pavlov strategy = new Pavlov();
        GameHistory history = new GameHistory(100);
        history.recordMove(Action.DEFECT, Action.DEFECT);
        
        assertEquals(Action.DEFECT, strategy.getAction(history));
    }
}
