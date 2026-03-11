package dev.ahwz.ipd.strategies;

import dev.ahwz.ipd.model.Action;
import dev.ahwz.ipd.model.GameHistory;
import dev.ahwz.ipd.model.Strategy;

public class AlwaysDefect implements Strategy {
    @Override
    public String getName() {
        return "Always Defect";
    }

    @Override
    public Action getAction(GameHistory history) {
        return Action.DEFECT;
    }
}
