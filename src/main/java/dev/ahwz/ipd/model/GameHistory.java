package dev.ahwz.ipd.model;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class GameHistory {
    private List<Action> playerActions;
    private List<Action> opponentActions;
    private int rounds;

    public GameHistory(int rounds) {
        this.playerActions = new ArrayList<>();
        this.opponentActions = new ArrayList<>();
        this.rounds = rounds;
    }

    public Action getLastOpponentAction() {
        return opponentActions.getLast();
    }

    public Action getLastPlayerAction() {
        return playerActions.getLast();
    }

    public boolean isFirstMove() {
        return playerActions.isEmpty();
    }

    public boolean isLastMove() {
        return playerActions.size() == rounds - 1;
    }

    /**
     * Returns n to last player action. If n is larger than number of actions returns null
     *
     * @param n distance to last action
     * @return Action or null
     */
    public Action getNToLastPlayerAction(int n) {
        if (n > playerActions.size()) return null;
        return playerActions.get(playerActions.size() - n);
    }

    /**
     * Returns n to last opponent action. If n is larger than number of actions returns null
     *
     * @param n distance to last action
     * @return Action or null
     */
    public Action getNToLastOpponentAction(int n) {
        if (n > opponentActions.size()) return null;
        return opponentActions.get(opponentActions.size() - n);
    }

    public void recordMove(Action playerMove, Action opponentMove) {
        playerActions.add(playerMove);
        opponentActions.add(opponentMove);
    }


    public List<Action> getPlayerActions() {
        return this.playerActions;
    }

    public List<Action> getOpponentActions() {
        return this.opponentActions;
    }

    public int getRounds() {
        return this.rounds;
    }
}
