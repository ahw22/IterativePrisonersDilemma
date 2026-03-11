package dev.ahwz.ipd.model;

public interface Strategy {
    String getName();
    Action getAction(GameHistory history);
}
