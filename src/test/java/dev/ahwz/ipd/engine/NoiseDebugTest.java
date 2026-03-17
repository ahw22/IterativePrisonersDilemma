package dev.ahwz.ipd.engine;

import dev.ahwz.ipd.model.*;
import dev.ahwz.ipd.strategies.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NoiseDebugTest {

    @Test
    void testTitForTatWithNoise() {
        Strategy titForTat = new TitForTat();
        Strategy alwaysDefect = new AlwaysDefect();
        
        // Manually trace what happens with noise
        GameHistory historyA = new GameHistory(100);  // TFT's history
        GameHistory historyB = new GameHistory(100); // AllDefect's history
        
        System.out.println("=== Manual trace with noise=1.0 (always noisy) ===");
        
        // Round 1
        Action moveA = titForTat.getAction(historyA); // C (first move)
        Action moveB = alwaysDefect.getAction(historyB); // D
        
        System.out.printf("Round 1: TFT intends=%s, AllDefect intends=%s%n", moveA, moveB);
        
        // Both noisy - they see opposite of what was played
        // TFT (A) plays C, AllDefect (B) plays D
        // Noise flips what each SEES
        // A sees: D flipped to C (opposite of D)
        // B sees: C flipped to D (opposite of C)
        historyA.recordMove(moveA, moveB.opposite()); // A sees D->C
        historyB.recordMove(moveB, moveA.opposite()); // B sees C->D
        
        System.out.printf("  TFT's history: last opponent action seen = %s%n", historyA.getLastOpponentAction());
        System.out.printf("  AllDefect's history: last opponent action seen = %s%n", historyB.getLastOpponentAction());
        
        // Round 2 - TFT should react to what it SAW, not what was played
        moveA = titForTat.getAction(historyA); // Should copy what A saw B play (C)
        moveB = alwaysDefect.getAction(historyB); // D
        
        System.out.printf("Round 2: TFT plays=%s (should be C if reacting to perceived C), AllDefect plays=%s%n", moveA, moveB);
        
        historyA.recordMove(moveA, moveB.opposite());
        historyB.recordMove(moveB, moveA.opposite());
        
        System.out.printf("  TFT's history: last opponent action seen = %s%n", historyA.getLastOpponentAction());
    }

    @Test
    void testWithNoNoiseDirect() {
        Strategy titForTat = new TitForTat();
        Strategy alwaysDefect = new AlwaysDefect();
        
        GameHistory historyA = new GameHistory(100);
        
        System.out.println("=== Manual trace with NO noise ===");
        
        // Round 1: TFT plays C, AllDefect plays D, no noise
        historyA.recordMove(Action.COOPERATE, Action.DEFECT);
        System.out.printf("After round 1: TFT sees opponent played %s%n", historyA.getLastOpponentAction());
        
        // Round 2: TFT should defect (copy what it saw)
        Action move2 = titForTat.getAction(historyA);
        System.out.printf("Round 2: TFT plays %s%n", move2);
        
        historyA.recordMove(move2, Action.DEFECT);
        
        // Round 3
        Action move3 = titForTat.getAction(historyA);
        System.out.printf("Round 3: TFT plays %s%n", move3);
    }

    @Test
    void testHistoryRecording() {
        GameHistory history = new GameHistory(100);
        
        history.recordMove(Action.COOPERATE, Action.DEFECT);
        assertEquals(Action.DEFECT, history.getLastOpponentAction());
        
        history.recordMove(Action.DEFECT, Action.COOPERATE);
        assertEquals(Action.COOPERATE, history.getLastOpponentAction());
        
        System.out.println("History test passed");
    }

    @Test
    void testNoiseWithForcedNoise() {
        // Force noise to be always applied by checking the behavior
        Strategy titForTat = new TitForTat();
        
        GameHistory history = new GameHistory(100);
        
        // First round: both cooperate
        history.recordMove(Action.COOPERATE, Action.COOPERATE);
        Action tftMove2 = titForTat.getAction(history);
        System.out.println("TFT Move 2 after coop: " + tftMove2);
        
        // Now record that opponent defected (what TFT sees)
        history.recordMove(Action.COOPERATE, Action.DEFECT);
        Action tftMove3 = titForTat.getAction(history);
        System.out.println("TFT Move 3 after defect: " + tftMove3);
        
        assertEquals(Action.COOPERATE, tftMove2);
        assertEquals(Action.DEFECT, tftMove3);
    }

    @Test
    void testWithNoNoise() {
        Strategy titForTat = new TitForTat();
        Strategy alwaysDefect = new AlwaysDefect();
        PayoffMatrix matrix = new PayoffMatrix(3, 5, 1, 0);

        Match match = new Match();
        MatchResult result = match.play(titForTat, alwaysDefect, 10, matrix, 0.0);

        System.out.println("=== With NO noise ===");
        for (int i = 0; i < result.roundResults().size(); i++) {
            RoundResult rr = result.roundResults().get(i);
            System.out.printf("Round %d: TFT=%s, AllDefect=%s%n",
                i + 1, rr.moveA(), rr.moveB());
        }
        System.out.println("Score TFT: " + result.scoreA());
        
        // TFT should start with C, then copy AllDefect's previous move
        // AllDefect always plays D, so TFT should play D after first round
    }

    @Test
    void testWithFullNoise() {
        Strategy titForTat = new TitForTat();
        Strategy alwaysDefect = new AlwaysDefect();
        PayoffMatrix matrix = new PayoffMatrix(3, 5, 1, 0);

        Match match = new Match();
        
        // Use a seed or run multiple times to see behavior
        System.out.println("=== Running multiple times with noise=0.5 ===");
        for (int run = 0; run < 3; run++) {
            MatchResult result = match.play(titForTat, alwaysDefect, 20, matrix, 0.5);
            System.out.printf("Run %d - Score TFT: %d, AllDefect: %d%n", 
                run + 1, result.scoreA(), result.scoreB());
        }
    }
}
