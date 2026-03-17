package dev.ahwz.ipd.model;

public enum Action {
    COOPERATE, DEFECT;

    public Action opposite() {
        if (this == DEFECT) return COOPERATE;
        return DEFECT;
    }
}
