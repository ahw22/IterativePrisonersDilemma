package dev.ahwz.ipd.strategies;

import dev.ahwz.ipd.model.Action;
import dev.ahwz.ipd.model.GameHistory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TitForTwoTatsTest {

    @Test
    void getName_returnsCorrectName() {
        TitForTwoTats strategy = new TitForTwoTats();
        assertEquals("Tit for two Tats", strategy.getName());
    }

    @Test
    void getAction_onFirstMove_returnsCooperate() {
        TitForTwoTats strategy = new TitForTwoTats();
        GameHistory history = new GameHistory(100);
        assertEquals(Action.COOPERATE, strategy.getAction(history));
    }

    @Test
    void getAction_afterSingleDefection_stillCooperates() {
        TitForTwoTats strategy = new TitForTwoTats();
        GameHistory history = new GameHistory(100);
        
        history.recordMove(Action.COOPERATE, Action.DEFECT);
        assertEquals(Action.COOPERATE, strategy.getAction(history));
    }

    @Test
    void getAction_afterTwoConsecutiveDefections_defects() {
        TitForTwoTats strategy = new TitForTwoTats();
        GameHistory history = new GameHistory(100);
        
        history.recordMove(Action.COOPERATE, Action.DEFECT);
        history.recordMove(Action.COOPERATE, Action.DEFECT);
        assertEquals(Action.DEFECT, strategy.getAction(history));
    }

    @Test
    void getAction_afterCooperatingAgain_afterTwoDefects_cooperates() {
        TitForTwoTats strategy = new TitForTwoTats();
        GameHistory history = new GameHistory(100);
        
        history.recordMove(Action.COOPERATE, Action.DEFECT);
        history.recordMove(Action.COOPERATE, Action.DEFECT);
        assertEquals(Action.DEFECT, strategy.getAction(history));
        
        history.recordMove(Action.DEFECT, Action.COOPERATE);
        assertEquals(Action.COOPERATE, strategy.getAction(history));
    }
}
