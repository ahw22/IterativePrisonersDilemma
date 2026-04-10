package dev.ahwz.ipd.engine;

import dev.ahwz.ipd.model.*;
import dev.ahwz.ipd.strategies.AlwaysDefect;
import dev.ahwz.ipd.strategies.SouthamptonStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SouthamptonDetectionTest {

    private final PayoffMatrix matrix = new PayoffMatrix(3, 5, 1, 0);
    private final Match match = new Match();

    @Test
    void play_southamptonSacrificeVsSouthamptonMaster_detectionWorks() {
        List<Action> code = List.of(
                Action.DEFECT, Action.DEFECT, Action.COOPERATE,
                Action.COOPERATE, Action.COOPERATE, Action.DEFECT,
                Action.DEFECT, Action.COOPERATE, Action.COOPERATE, Action.DEFECT);
        double certaintyThreshold = 0.9;

        SouthamptonStrategy sacrifice = new SouthamptonStrategy(code, certaintyThreshold, true);
        SouthamptonStrategy master = new SouthamptonStrategy(code, certaintyThreshold, false);

        MatchResult result = match.play(sacrifice, master, 20, matrix, 0.0);

        double coopRateMaster = result.coopRateB();
        double coopRateSacrifice = result.coopRateA();

        assertTrue(coopRateMaster < 0.5,
                "Master should mostly defect after detection, got: " + coopRateMaster);
        assertTrue(coopRateSacrifice > 0.5,
                "Sacrifice should mostly cooperate after detection, got: " + coopRateSacrifice);
    }

    @Test
    void play_southamptonMasterVsSouthamptonSacrifice_detectionWorks() {
        List<Action> code = List.of(
                Action.DEFECT, Action.DEFECT, Action.COOPERATE,
                Action.COOPERATE, Action.COOPERATE, Action.DEFECT,
                Action.DEFECT, Action.COOPERATE, Action.COOPERATE, Action.DEFECT);
        double certaintyThreshold = 0.9;

        SouthamptonStrategy master = new SouthamptonStrategy(code, certaintyThreshold, false);
        SouthamptonStrategy sacrifice = new SouthamptonStrategy(code, certaintyThreshold, true);

        MatchResult result = match.play(master, sacrifice, 20, matrix, 0.0);

        double coopRateMaster = result.coopRateA();
        double coopRateSacrifice = result.coopRateB();

        assertTrue(coopRateMaster < coopRateSacrifice,
                "Master should defect more than Sacrifice, got master: " + coopRateMaster + ", sacrifice: " + coopRateSacrifice);
    }

    @Test
    void play_southamptonVsDifferentStrategy_noDetection() {
        List<Action> code = List.of(
                Action.DEFECT, Action.DEFECT, Action.COOPERATE,
                Action.COOPERATE, Action.COOPERATE, Action.DEFECT,
                Action.DEFECT, Action.COOPERATE, Action.COOPERATE, Action.DEFECT);
        double certaintyThreshold = 0.9;

        SouthamptonStrategy sacrifice = new SouthamptonStrategy(code, certaintyThreshold, true);
        SouthamptonStrategy master = new SouthamptonStrategy(code, certaintyThreshold, false);
        Strategy differentStrategy = new AlwaysDefect();

        MatchResult result1 = match.play(sacrifice, differentStrategy, 20, matrix, 0.0);
        MatchResult result2 = match.play(master, differentStrategy, 20, matrix, 0.0);

        assertTrue(result1.coopRateA() < 0.5,
                "Sacrifice vs different strategy should not detect partner, got: " + result1.coopRateA());
        assertTrue(result2.coopRateA() < 0.5,
                "Master vs different strategy should not detect partner, got: " + result2.coopRateA());
    }

    @Test
    void play_twoSouthamptonStrategies_extendedRounds() {
        List<Action> code = List.of(
                Action.DEFECT, Action.DEFECT, Action.COOPERATE,
                Action.COOPERATE, Action.COOPERATE, Action.DEFECT,
                Action.DEFECT, Action.COOPERATE, Action.COOPERATE, Action.DEFECT);
        double certaintyThreshold = 0.9;

        SouthamptonStrategy sacrifice = new SouthamptonStrategy(code, certaintyThreshold, true);
        SouthamptonStrategy master = new SouthamptonStrategy(code, certaintyThreshold, false);

        MatchResult result = match.play(sacrifice, master, 100, matrix, 0.0);

        double coopRateSacrifice = result.coopRateA();
        double coopRateMaster = result.coopRateB();

        assertTrue(coopRateMaster < 0.5,
                "Master should mostly defect after detection, got: " + coopRateMaster);
        assertTrue(coopRateSacrifice > 0.5,
                "Sacrifice should mostly cooperate after detection, got: " + coopRateSacrifice);
    }
}