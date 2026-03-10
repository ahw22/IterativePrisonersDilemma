package dev.ahwz.strategies;

import dev.ahwz.model.Action;
import dev.ahwz.model.GameHistory;
import dev.ahwz.model.Strategy;

public class AlwaysCooporate implements Strategy {

    @Override
    public String getName() {
        return "Always Cooperate";
    }

    @Override
    public Action getAction(GameHistory history) {
        return Action.COOPERATE;
    }
}
