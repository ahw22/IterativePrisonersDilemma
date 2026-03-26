package dev.ahwz.ipd.strategies;

import dev.ahwz.ipd.model.Action;
import dev.ahwz.ipd.model.GameHistory;
import dev.ahwz.ipd.model.Strategy;

public class AlwaysCooporate implements Strategy {

    @Override
    public String getName() {
        return "Always Cooperate";
    }

    @Override
    public Action getAction(GameHistory history) {
        return Action.COOPERATE;
    }

    @Override
    public void reset() {}
}
