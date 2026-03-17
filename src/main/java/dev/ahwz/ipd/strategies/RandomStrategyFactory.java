package dev.ahwz.ipd.strategies;

import dev.ahwz.ipd.model.Strategy;

import java.util.List;

public class RandomStrategyFactory {
    public static Strategy createRandomStrategy(double randomness) {
        return new RandomStrategy(randomness);
    }

    public static List<Strategy> createRandomStrategiesList() {
        return List.of(
                new RandomStrategy(0.25),
                new RandomStrategy(0.5),
                new RandomStrategy(0.75)
        );
    }
}
