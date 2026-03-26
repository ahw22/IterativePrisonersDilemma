package dev.ahwz.ipd.strategies;

import dev.ahwz.ipd.model.Action;
import dev.ahwz.ipd.model.GameHistory;
import dev.ahwz.ipd.model.Strategy;

public class GenerousTitForTat implements Strategy {
    @Override
    public String getName() {
        return "Generous Tit for Tat";
    }

    @Override
    public Action getAction(GameHistory history) {
        if (history.isFirstMove()) {
            return Action.COOPERATE;
        }
        if (history.getLastOpponentAction() == Action.COOPERATE) {
            return Action.COOPERATE;
        } else if (Math.random() < 0.33) {
            return Action.COOPERATE;
        } else {
            return Action.DEFECT;
        }
    }

    @Override
    public void reset() {}
}
