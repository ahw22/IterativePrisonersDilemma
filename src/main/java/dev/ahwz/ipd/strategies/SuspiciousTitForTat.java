package dev.ahwz.ipd.strategies;

import dev.ahwz.ipd.model.Action;
import dev.ahwz.ipd.model.GameHistory;
import dev.ahwz.ipd.model.Strategy;

public class SuspiciousTitForTat implements Strategy {
    @Override
    public String getName() {
        return "Suspicious Tit For Tat";
    }

    @Override
    public Action getAction(GameHistory history) {
        if (history.isFirstMove()) {
            return Action.DEFECT;
        }
        if (history.getLastOpponentAction() == Action.COOPERATE) {
            return Action.COOPERATE;
        } else {
            return Action.DEFECT;
        }
    }

    @Override
    public void reset() {}
}
