package dev.ahwz.ipd.engine;

import dev.ahwz.ipd.model.*;
import dev.ahwz.ipd.strategies.AlwaysCooperate;
import dev.ahwz.ipd.strategies.AlwaysDefect;
import dev.ahwz.ipd.strategies.TitForTat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatchTest {

    private final PayoffMatrix matrix = new PayoffMatrix(3, 5, 1, 0);
    private final Match match = new Match();

    @Test
    void play_alwaysCooperateVsAlwaysDefect_correctScores() {
        Strategy playerA = new AlwaysCooperate();
        Strategy playerB = new AlwaysDefect();
        
        MatchResult result = match.play(playerA, playerB, 100, matrix, 0.0);
        
        assertEquals(0, result.scoreA());
        assertEquals(500, result.scoreB());
        assertEquals(1.0, result.coopRateA(), 0.01);
        assertEquals(0.0, result.coopRateB(), 0.01);
    }

    @Test
    void play_alwaysCooperateVsAlwaysCooperate_correctScores() {
        Strategy playerA = new AlwaysCooperate();
        Strategy playerB = new AlwaysCooperate();
        
        MatchResult result = match.play(playerA, playerB, 100, matrix, 0.0);
        
        assertEquals(300, result.scoreA());
        assertEquals(300, result.scoreB());
        assertEquals(1.0, result.coopRateA(), 0.01);
        assertEquals(1.0, result.coopRateB(), 0.01);
    }

    @Test
    void play_alwaysDefectVsAlwaysDefect_correctScores() {
        Strategy playerA = new AlwaysDefect();
        Strategy playerB = new AlwaysDefect();
        
        MatchResult result = match.play(playerA, playerB, 100, matrix, 0.0);
        
        assertEquals(100, result.scoreA());
        assertEquals(100, result.scoreB());
        assertEquals(0.0, result.coopRateA(), 0.01);
        assertEquals(0.0, result.coopRateB(), 0.01);
    }

    @Test
    void play_titForTatVsAlwaysCooperate_highCooperation() {
        Strategy playerA = new TitForTat();
        Strategy playerB = new AlwaysCooperate();
        
        MatchResult result = match.play(playerA, playerB, 100, matrix, 0.0);
        
        assertEquals(300, result.scoreA());
        assertEquals(300, result.scoreB());
        assertEquals(1.0, result.coopRateA(), 0.01);
        assertEquals(1.0, result.coopRateB(), 0.01);
    }

    @Test
    void play_titForTatVsAlwaysDefect_mutualDefection() {
        Strategy playerA = new TitForTat();
        Strategy playerB = new AlwaysDefect();
        
        MatchResult result = match.play(playerA, playerB, 100, matrix, 0.0);
        
        assertEquals(99, result.scoreA());
        assertEquals(104, result.scoreB());
    }

    @Test
    void play_correctNumberOfRounds() {
        Strategy playerA = new AlwaysCooperate();
        Strategy playerB = new AlwaysDefect();
        
        MatchResult result = match.play(playerA, playerB, 50, matrix, 0.0);
        
        assertEquals(50, result.rounds());
        assertEquals(50, result.roundResults().size());
    }

    @Test
    void play_returnsBothPlayers() {
        Strategy playerA = new AlwaysCooperate();
        Strategy playerB = new AlwaysDefect();
        
        MatchResult result = match.play(playerA, playerB, 10, matrix, 0.0);
        
        assertEquals(2, result.getPlayers().size());
    }

    @Test
    void play_titForTatVsTitForTat_mutualCooperation() {
        Strategy playerA = new TitForTat();
        Strategy playerB = new TitForTat();
        
        MatchResult result = match.play(playerA, playerB, 100, matrix, 0.0);
        
        assertEquals(300, result.scoreA());
        assertEquals(300, result.scoreB());
        assertEquals(1.0, result.coopRateA(), 0.01);
        assertEquals(1.0, result.coopRateB(), 0.01);
    }
}
