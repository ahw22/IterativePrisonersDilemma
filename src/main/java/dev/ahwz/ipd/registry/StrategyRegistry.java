package dev.ahwz.ipd.registry;

import dev.ahwz.ipd.model.Strategy;
import dev.ahwz.ipd.strategies.*;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public final class StrategyRegistry {

    private static final List<Strategy> STRATEGIES = List.of(
            new AlwaysCooporate(),
            new AlwaysDefect(),
            new GenerousTitForTat(),
            new GrimTrigger(),
            new Pavlov(),
            new RandomStrategy(),
            new SuspiciousTitForTat(),
            new TitForTat(),
            new TitForTwoTats()
    );

    public static List<Strategy> getStrategies() {
        return STRATEGIES;
    }

    public static Strategy getStrategy(String name) {
        return STRATEGIES.stream().filter(strategy -> strategy.getName().equals(name)).findFirst().orElse(null);
    }
}
