package dev.ahwz.ipd.strategies;

import dev.ahwz.ipd.model.Action;
import dev.ahwz.ipd.model.GameHistory;
import dev.ahwz.ipd.model.Strategy;

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
