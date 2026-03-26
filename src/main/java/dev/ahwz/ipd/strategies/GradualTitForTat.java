package dev.ahwz.ipd.strategies;

import dev.ahwz.ipd.model.Action;
import dev.ahwz.ipd.model.GameHistory;
import dev.ahwz.ipd.model.Strategy;

public class GradualTitForTat implements Strategy {

    private int defectionCount = 0;
    private int cooperationCount = 0;

    @Override
    public String getName() {
        return "Gradual Tit for Tat";
    }

    @Override
    public Action getAction(GameHistory history) {
        if (history.isFirstMove()) {
            return Action.COOPERATE;
        }

        Action lastOpponent = history.getLastOpponentAction();

        if (lastOpponent == Action.DEFECT) {
            defectionCount++;
            cooperationCount = 0;
            return Action.DEFECT;
        } else {
            if (cooperationCount < defectionCount) {
                cooperationCount++;
                return Action.COOPERATE;
            } else {
                defectionCount = 0;
                cooperationCount = 0;
                return Action.COOPERATE;
            }
        }
    }

    @Override
    public void reset() {
        defectionCount = 0;
        cooperationCount = 0;
    }
}
