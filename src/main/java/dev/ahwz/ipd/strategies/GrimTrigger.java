package dev.ahwz.ipd.strategies;

import dev.ahwz.ipd.model.Action;
import dev.ahwz.ipd.model.GameHistory;
import dev.ahwz.ipd.model.Strategy;

public class GrimTrigger implements Strategy {
    @Override
    public String getName() {
        return "Grim Trigger";
    }

    @Override
    public Action getAction(GameHistory history) {
        if (history.isFirstMove()) {
            return Action.COOPERATE;
        }
        if (history.getOpponentActions().contains(Action.DEFECT)) {
            return Action.DEFECT;
        } else {
            return Action.COOPERATE;
        }
    }

    @Override
    public void reset() {}
}
