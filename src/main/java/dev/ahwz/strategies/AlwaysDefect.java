package dev.ahwz.strategies;

import dev.ahwz.model.Action;
import dev.ahwz.model.GameHistory;
import dev.ahwz.model.Strategy;

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
