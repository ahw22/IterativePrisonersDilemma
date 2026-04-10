package dev.ahwz.ipd.registry;

import dev.ahwz.ipd.model.Strategy;
import dev.ahwz.ipd.strategies.*;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@NoArgsConstructor
public final class StrategyRegistry {

    private static final List<Strategy> STRATEGIES = Stream.of(
            List.of(
                    new AlwaysCooperate(),
                    new AlwaysDefect(),
                    new GenerousTitForTat(),
                    new GradualTitForTat(),
                    new GrimTrigger(),
                    new Pavlov(),
                    new SuspiciousTitForTat(),
                    new TitForTat(),
                    new TitForTwoTats()
            ),
            RandomStrategyFactory.createRandomStrategiesList(),
            SouthamptonFactory.createSouthamptonStrategy()          // just add a new line here
    ).flatMap(Collection::stream).toList();

    public static List<Strategy> getStrategies() {
        return STRATEGIES;
    }

    public static Strategy getStrategy(String name) {
        return STRATEGIES.stream().filter(strategy -> strategy.getName().equals(name)).findFirst().orElse(null);
    }
}
