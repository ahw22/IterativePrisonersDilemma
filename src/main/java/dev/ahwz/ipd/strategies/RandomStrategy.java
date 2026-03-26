package dev.ahwz.ipd.strategies;

import dev.ahwz.ipd.model.Action;
import dev.ahwz.ipd.model.GameHistory;
import dev.ahwz.ipd.model.Strategy;

public class RandomStrategy implements Strategy {

    private final double randomness;

    public RandomStrategy(double randomness) {
        this.randomness = randomness;
    }

    @Override
    public String getName() {
        return "Random(" + randomness + ")";
    }

    @Override
    public Action getAction(GameHistory history) {
       return (Math.random() < randomness) ? Action.COOPERATE : Action.DEFECT;
    }

    @Override
    public void reset() {}
}
