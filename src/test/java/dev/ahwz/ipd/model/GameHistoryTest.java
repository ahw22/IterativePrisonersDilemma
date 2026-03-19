package dev.ahwz.ipd.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameHistoryTest {

    @Test
    void isFirstMove_onEmptyHistory_returnsTrue() {
        GameHistory history = new GameHistory(100);
        assertTrue(history.isFirstMove());
    }

    @Test
    void isFirstMove_afterRecordingMove_returnsFalse() {
        GameHistory history = new GameHistory(100);
        history.recordMove(Action.COOPERATE, Action.COOPERATE);
        assertFalse(history.isFirstMove());
    }

    @Test
    void getLastPlayerAction_afterMove_returnsCorrectAction() {
        GameHistory history = new GameHistory(100);
        history.recordMove(Action.DEFECT, Action.COOPERATE);
        assertEquals(Action.DEFECT, history.getLastPlayerAction());
    }

    @Test
    void getLastOpponentAction_afterMove_returnsCorrectAction() {
        GameHistory history = new GameHistory(100);
        history.recordMove(Action.COOPERATE, Action.DEFECT);
        assertEquals(Action.DEFECT, history.getLastOpponentAction());
    }

    @Test
    void getPlayerActions_afterMultipleMoves_returnsAllActions() {
        GameHistory history = new GameHistory(100);
        history.recordMove(Action.COOPERATE, Action.COOPERATE);
        history.recordMove(Action.DEFECT, Action.COOPERATE);
        history.recordMove(Action.COOPERATE, Action.DEFECT);

        List<Action> actions = history.getPlayerActions();
        assertEquals(3, actions.size());
        assertEquals(Action.COOPERATE, actions.get(0));
        assertEquals(Action.DEFECT, actions.get(1));
        assertEquals(Action.COOPERATE, actions.get(2));
    }

    @Test
    void getNToLastPlayerAction_withValidN_returnsCorrectAction() {
        GameHistory history = new GameHistory(100);
        history.recordMove(Action.COOPERATE, Action.COOPERATE);
        history.recordMove(Action.DEFECT, Action.COOPERATE);
        history.recordMove(Action.COOPERATE, Action.DEFECT);

        assertEquals(Action.COOPERATE, history.getNToLastPlayerAction(1));
        assertEquals(Action.DEFECT, history.getNToLastPlayerAction(2));
        assertEquals(Action.COOPERATE, history.getNToLastPlayerAction(3));
    }

    @Test
    void getNToLastPlayerAction_withNTooLarge_returnsNull() {
        GameHistory history = new GameHistory(100);
        history.recordMove(Action.COOPERATE, Action.COOPERATE);

        assertNull(history.getNToLastPlayerAction(5));
    }

    @Test
    void isLastMove_whenNotAtLastRound_returnsFalse() {
        GameHistory history = new GameHistory(100);
        history.recordMove(Action.COOPERATE, Action.COOPERATE);
        assertFalse(history.isLastMove());
    }

    @Test
    void getRounds_returnsConfiguredValue() {
        GameHistory history = new GameHistory(50);
        assertEquals(50, history.getRounds());
    }
}
