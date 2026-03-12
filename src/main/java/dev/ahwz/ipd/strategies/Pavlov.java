package dev.ahwz.ipd.strategies;

import dev.ahwz.ipd.model.Action;
import dev.ahwz.ipd.model.GameHistory;
import dev.ahwz.ipd.model.Strategy;

public class Pavlov implements Strategy {
    @Override
    public String getName() {
        return "Pavlov";
    }

    @Override
    public Action getAction(GameHistory history) {
        if (history.isFirstMove()) {
            return Action.COOPERATE;
        }
        if (wonLastRound(history)) {
            return Action.COOPERATE;
        } else {
            return Action.DEFECT;
        }
    }

    private boolean wonLastRound(GameHistory history) {
        Action lastPlayerAction = history.getLastPlayerAction();
        Action lastOpponentAction = history.getLastOpponentAction();
        if (lastPlayerAction == Action.COOPERATE && lastOpponentAction == Action.COOPERATE) {
            return true;
        } else {
            return lastPlayerAction == Action.DEFECT && lastOpponentAction == Action.COOPERATE;
        }
    }
}
