package dev.ahwz.ipd.strategies;

import dev.ahwz.ipd.model.Action;
import dev.ahwz.ipd.model.GameHistory;
import dev.ahwz.ipd.model.Strategy;

import java.util.List;

public class TitForTwoTats implements Strategy {
    @Override
    public String getName() {
        return "Tit for two Tats";
    }

    @Override
    public Action getAction(GameHistory history) {
        if (history.isFirstMove()) {
            return Action.COOPERATE;
        }
        List<Action> opponentActions = history.getOpponentActions();
        if (history.getLastOpponentAction() == Action.DEFECT && history.getNToLastOpponentAction(2) == Action.DEFECT) {
            return Action.DEFECT;
        } else {
            return Action.COOPERATE;
        }
    }

    @Override
    public void reset() {}
}
