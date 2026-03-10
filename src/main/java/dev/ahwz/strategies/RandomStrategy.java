package dev.ahwz.strategies;

import dev.ahwz.model.Action;
import dev.ahwz.model.GameHistory;
import dev.ahwz.model.Strategy;

public class RandomStrategy implements Strategy {
    @Override
    public String getName() {
        return "Random";
    }

    @Override
    public Action getAction(GameHistory history) {
       return (Math.random() < 0.5) ? Action.COOPERATE : Action.DEFECT;
    }
}
