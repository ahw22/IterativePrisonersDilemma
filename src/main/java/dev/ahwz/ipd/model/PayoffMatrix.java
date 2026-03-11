package dev.ahwz.ipd.model;

// Formula:  T > R > P > S && R > (S + T)/2
// Default= R = 3, T = 5, P = 1, S = 0
public record PayoffMatrix(
        int reward,
        int temptation,
        int punishment,
        int sucker
) {

    public int payoff(Action me, Action opponent) {
        return switch (me) {
            case COOPERATE -> (opponent == Action.COOPERATE) ? reward : sucker;
            case DEFECT -> (opponent == Action.COOPERATE) ? temptation : punishment;
        };
    }
}