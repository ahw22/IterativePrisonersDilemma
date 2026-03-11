package dev.ahwz.ipd.strategies;

import dev.ahwz.ipd.model.Action;
import dev.ahwz.ipd.model.GameHistory;
import dev.ahwz.ipd.model.Strategy;

public class TitForTat implements Strategy {

    @Override
    public String getName() {
        return "Tit for Tat";
    }

    @Override
    public Action getAction(GameHistory history) {
        if (history.getPlayerActions().isEmpty()) {
            return Action.COOPERATE;
        }
        if (history.getLastOpponentAction() == Action.COOPERATE) {
            return Action.COOPERATE;
        } else {
            return Action.DEFECT;
        }
    }
}
