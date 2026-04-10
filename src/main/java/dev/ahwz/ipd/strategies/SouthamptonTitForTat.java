package dev.ahwz.ipd.strategies;

import dev.ahwz.ipd.model.Action;
import dev.ahwz.ipd.model.GameHistory;
import dev.ahwz.ipd.model.Strategy;

import java.util.List;

/** I decided to try to combine the Southampton Strategy but combine it with GradualTFT.
 */
public class SouthamptonTitForTat implements Strategy {

    private List<Action> code;
    private double certaintyThreshold;
    private int round = 0;
    private boolean isSacrifice;
    private boolean partnerDetected;
    private boolean detectionChecked;

    public SouthamptonTitForTat(List<Action> code, double certaintyThreshold, boolean isSacrifice) {
        this.code = code;
        this.certaintyThreshold = certaintyThreshold;
        this.isSacrifice = isSacrifice;
        this.partnerDetected = false;
        this.detectionChecked = false;
    }

    @Override
    public String getName() {
        return "Southampton Tit for Tat " + (isSacrifice ? "Sacrifice" : "Master");
    }

    @Override
    public Action getAction(GameHistory history) {
        if (round < code.size()) {
            return code.get(round++);
        }
        if (!detectionChecked) {
            checkIfOpponentIsPartner(history);
            detectionChecked = true;
        }
        if (partnerDetected) {
            return (isSacrifice ? Action.COOPERATE : Action.DEFECT);
        }
        if (history.getLastOpponentAction() == Action.COOPERATE) {
            return Action.COOPERATE;
        } else if (Math.random() < 0.33) {
            return Action.COOPERATE;
        } else {
            return Action.DEFECT;
        }
    }

    private void checkIfOpponentIsPartner(GameHistory history) {
        List<Action> opponentActions = history.getOpponentActions();
        if (opponentActions.size() < code.size()) {
            return;
        }
        int matches = 0;
        for (int i = 0; i < code.size(); i++) {
            if (code.get(i) == opponentActions.get(i)) {
                matches++;
            }
        }
        if ((double) matches / code.size() > certaintyThreshold) {
            partnerDetected = true;
        }
    }

    @Override
    public void reset() {
        round = 0;
        partnerDetected = false;
        detectionChecked = false;
    }
}

