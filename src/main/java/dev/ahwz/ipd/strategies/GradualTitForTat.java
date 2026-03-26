package dev.ahwz.ipd.strategies;

import dev.ahwz.ipd.model.Action;
import dev.ahwz.ipd.model.GameHistory;
import dev.ahwz.ipd.model.Strategy;

public class GradualTitForTat implements Strategy {

    private boolean punishing = false;
    private boolean calming = false;
    private int punishmentCount = 0;
    private int punishmentLimit = 0;

    @Override
    public String getName() {
        return "Gradual Tit for Tat";
    }

    @Override
    public Action getAction(GameHistory history) {
        if (punishing) {
            Action lastOpponent = history.getLastOpponentAction();
            if (lastOpponent == Action.DEFECT) {
                punishmentLimit++;
            }
            if (punishmentCount < punishmentLimit) {
                punishmentCount++;
                return Action.DEFECT;
            } else {
                punishing = false;
                calming = true;
                punishmentCount = 0;
                return Action.COOPERATE;
            }
        }
        if (calming) {
            calming = false;
            if (history.getLastOpponentAction() == Action.DEFECT) {
                punishing = true;
                punishmentCount = 1;
                punishmentLimit = 1;
                return Action.DEFECT;
            }
            return Action.COOPERATE;
        }
        if (history.getLastOpponentAction() == Action.DEFECT) {
            punishing = true;
            punishmentCount = 1;
            punishmentLimit = 1;
            return Action.DEFECT;
        }

        return Action.COOPERATE;
    }

    @Override
    public void reset() {
        punishing = false;
        calming = false;
        punishmentCount = 0;
        punishmentLimit = 0;
    }
}
