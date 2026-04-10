package dev.ahwz.ipd.strategies;

import dev.ahwz.ipd.model.Action;
import dev.ahwz.ipd.model.Strategy;

import java.util.ArrayList;
import java.util.List;

public class SouthamptonFactory {

    public static List<Strategy> createSouthamptonStrategy() {
        double certaintyThreshold = 0.9;
        List<Action> code = generateRandomCode(10);
        List<Action> code2 = generateRandomCode(10);
        return List.of(new SouthamptonStrategy(code, certaintyThreshold, false),
                new SouthamptonStrategy(code, certaintyThreshold, true),
                new SouthamptonTitForTat(code2, certaintyThreshold, false),
                new SouthamptonTitForTat(code2, certaintyThreshold, true));
    }

    private static List<Action> generateRandomCode(int length) {
        List<Action> code = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            code.add((Math.random() < 0.5) ? Action.COOPERATE : Action.DEFECT);
        }
        return code;
    }
}
