package dev.ahwz.ipd.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
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
    private boolean isFirstMove() {
        return playerActions.isEmpty();
    }
    private boolean isLastMove() {
        return playerActions.size() == rounds - 1;
    }

    public void recordMove(Action playerMove, Action opponentMove) {
        playerActions.add(playerMove);
        opponentActions.add(opponentMove);
    }


}
