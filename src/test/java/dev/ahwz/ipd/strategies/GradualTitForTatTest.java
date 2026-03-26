package dev.ahwz.ipd.strategies;

import dev.ahwz.ipd.model.Action;
import dev.ahwz.ipd.model.GameHistory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GradualTitForTatTest {

    @Test
    void getName_returnsCorrectName() {
        GradualTitForTat strategy = new GradualTitForTat();
        assertEquals("Gradual Tit for Tat", strategy.getName());
    }

    @Test
    void reset_clearsAllState() {
        GradualTitForTat strategy = new GradualTitForTat();
        GameHistory history = new GameHistory(100);

        history.recordMove(Action.COOPERATE, Action.DEFECT);
        strategy.getAction(history);

        history.recordMove(Action.DEFECT, Action.COOPERATE);
        strategy.getAction(history);

        strategy.reset();

        GameHistory freshHistory = new GameHistory(100);
        assertEquals(Action.COOPERATE, strategy.getAction(freshHistory));
    }

    @Test
    void getAction_onFirstMove_returnsCooperate() {
        GradualTitForTat strategy = new GradualTitForTat();
        GameHistory history = new GameHistory(100);
        assertEquals(Action.COOPERATE, strategy.getAction(history));
    }

    @Test
    void getAction_afterOpponentCooperated_followingFirstMove_returnsCooperate() {
        GradualTitForTat strategy = new GradualTitForTat();
        GameHistory history = new GameHistory(100);
        history.recordMove(Action.COOPERATE, Action.COOPERATE);
        assertEquals(Action.COOPERATE, strategy.getAction(history));
    }

    @Test
    void getAction_afterSingleDefection_retaliatesWithOneDefection() {
        GradualTitForTat strategy = new GradualTitForTat();
        GameHistory history = new GameHistory(100);

        history.recordMove(Action.COOPERATE, Action.DEFECT);
        assertEquals(Action.DEFECT, strategy.getAction(history));
    }

    @Test
    void getAction_afterSingleDefection_calmsWithOneCooperation() {
        GradualTitForTat strategy = new GradualTitForTat();
        GameHistory history = new GameHistory(100);

        history.recordMove(Action.COOPERATE, Action.DEFECT);
        assertEquals(Action.DEFECT, strategy.getAction(history));

        history.recordMove(Action.DEFECT, Action.COOPERATE);
        assertEquals(Action.COOPERATE, strategy.getAction(history));
    }

    @Test
    void getAction_afterCalmingPeriod_returnsToNormalCooperation() {
        GradualTitForTat strategy = new GradualTitForTat();
        GameHistory history = new GameHistory(100);

        history.recordMove(Action.COOPERATE, Action.DEFECT);
        strategy.getAction(history);

        history.recordMove(Action.DEFECT, Action.COOPERATE);
        strategy.getAction(history);

        history.recordMove(Action.COOPERATE, Action.COOPERATE);
        assertEquals(Action.COOPERATE, strategy.getAction(history));
    }

    @Test
    void getAction_afterTwoConsecutiveDefections_retaliatesWithTwoDefections() {
        GradualTitForTat strategy = new GradualTitForTat();
        GameHistory history = new GameHistory(100);

        history.recordMove(Action.COOPERATE, Action.DEFECT);
        assertEquals(Action.DEFECT, strategy.getAction(history));

        history.recordMove(Action.DEFECT, Action.DEFECT);
        assertEquals(Action.DEFECT, strategy.getAction(history));
    }

    @Test
    void getAction_afterTwoDefections_calmsWithOneCooperation() {
        GradualTitForTat strategy = new GradualTitForTat();
        GameHistory history = new GameHistory(100);

        history.recordMove(Action.COOPERATE, Action.DEFECT);
        history.recordMove(Action.DEFECT, Action.DEFECT);
        history.recordMove(Action.DEFECT, Action.COOPERATE);

        assertEquals(Action.COOPERATE, strategy.getAction(history));

        history.recordMove(Action.COOPERATE, Action.COOPERATE);
        assertEquals(Action.COOPERATE, strategy.getAction(history));
    }

    @Test
    void getAction_afterThreeConsecutiveDefections_retaliatesWithThreeDefections() {
        GradualTitForTat strategy = new GradualTitForTat();
        GameHistory history = new GameHistory(100);

        history.recordMove(Action.COOPERATE, Action.DEFECT);
        assertEquals(Action.DEFECT, strategy.getAction(history));

        history.recordMove(Action.DEFECT, Action.DEFECT);
        assertEquals(Action.DEFECT, strategy.getAction(history));

        history.recordMove(Action.DEFECT, Action.DEFECT);
        assertEquals(Action.DEFECT, strategy.getAction(history));
    }

    @Test
    void getAction_afterThreeDefections_calmsWithOneCooperation() {
        GradualTitForTat strategy = new GradualTitForTat();
        GameHistory history = new GameHistory(100);

        history.recordMove(Action.COOPERATE, Action.DEFECT);
        history.recordMove(Action.DEFECT, Action.DEFECT);
        history.recordMove(Action.DEFECT, Action.DEFECT);
        history.recordMove(Action.DEFECT, Action.COOPERATE);

        assertEquals(Action.COOPERATE, strategy.getAction(history));
    }

    @Test
    void getAction_newDefectionDuringCalmingStartsNewEpisode() {
        GradualTitForTat strategy = new GradualTitForTat();
        GameHistory history = new GameHistory(100);

        history.recordMove(Action.COOPERATE, Action.DEFECT);
        assertEquals(Action.DEFECT, strategy.getAction(history));

        history.recordMove(Action.DEFECT, Action.COOPERATE);
        assertEquals(Action.COOPERATE, strategy.getAction(history));

        history.recordMove(Action.COOPERATE, Action.DEFECT);
        assertEquals(Action.DEFECT, strategy.getAction(history));
    }

    @Test
    void getAction_opponentDefectsAgainAfterCalmingResetsEpisode() {
        GradualTitForTat strategy = new GradualTitForTat();
        GameHistory history = new GameHistory(100);

        history.recordMove(Action.COOPERATE, Action.DEFECT);
        assertEquals(Action.DEFECT, strategy.getAction(history));

        history.recordMove(Action.DEFECT, Action.COOPERATE);
        assertEquals(Action.COOPERATE, strategy.getAction(history));

        history.recordMove(Action.COOPERATE, Action.COOPERATE);
        assertEquals(Action.COOPERATE, strategy.getAction(history));

        history.recordMove(Action.COOPERATE, Action.DEFECT);
        assertEquals(Action.DEFECT, strategy.getAction(history));

        history.recordMove(Action.DEFECT, Action.COOPERATE);
        assertEquals(Action.COOPERATE, strategy.getAction(history));
    }

    @Test
    void getAction_multipleDefectionEpisodes_worksCorrectly() {
        GradualTitForTat strategy = new GradualTitForTat();
        GameHistory history = new GameHistory(100);

        history.recordMove(Action.COOPERATE, Action.COOPERATE);

        history.recordMove(Action.COOPERATE, Action.DEFECT);
        assertEquals(Action.DEFECT, strategy.getAction(history));

        history.recordMove(Action.DEFECT, Action.COOPERATE);
        assertEquals(Action.COOPERATE, strategy.getAction(history));

        history.recordMove(Action.COOPERATE, Action.COOPERATE);

        history.recordMove(Action.COOPERATE, Action.DEFECT);
        history.recordMove(Action.DEFECT, Action.DEFECT);
        assertEquals(Action.DEFECT, strategy.getAction(history));

        history.recordMove(Action.DEFECT, Action.COOPERATE);
        assertEquals(Action.COOPERATE, strategy.getAction(history));
    }

    @Test
    void getAction_respondsCorrectlyWhenOpponentAlternates() {
        GradualTitForTat strategy = new GradualTitForTat();
        GameHistory history = new GameHistory(100);

        history.recordMove(Action.COOPERATE, Action.COOPERATE);
        assertEquals(Action.COOPERATE, strategy.getAction(history));

        history.recordMove(Action.COOPERATE, Action.DEFECT);
        assertEquals(Action.DEFECT, strategy.getAction(history));

        history.recordMove(Action.DEFECT, Action.COOPERATE);
        assertEquals(Action.COOPERATE, strategy.getAction(history));

        history.recordMove(Action.COOPERATE, Action.DEFECT);
        assertEquals(Action.DEFECT, strategy.getAction(history));
    }

    @Test
    void getAction_stateIndependentAcrossInstances() {
        GradualTitForTat strategy1 = new GradualTitForTat();
        GradualTitForTat strategy2 = new GradualTitForTat();
        GameHistory history1 = new GameHistory(100);
        GameHistory history2 = new GameHistory(100);

        history1.recordMove(Action.COOPERATE, Action.DEFECT);
        assertEquals(Action.DEFECT, strategy1.getAction(history1));
        assertEquals(Action.COOPERATE, strategy2.getAction(history2));
    }

    @Test
    void getAction_longPunishmentSequence_followedByCalm() {
        GradualTitForTat strategy = new GradualTitForTat();
        GameHistory history = new GameHistory(100);

        for (int i = 0; i < 5; i++) {
            history.recordMove(Action.COOPERATE, Action.DEFECT);
            assertEquals(Action.DEFECT, strategy.getAction(history));
        }

        history.recordMove(Action.DEFECT, Action.COOPERATE);
        assertEquals(Action.COOPERATE, strategy.getAction(history));
    }
}
