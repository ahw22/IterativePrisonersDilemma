package dev.ahwz.model;

public interface Strategy {
    String getName();
    Action getAction(GameHistory history);
}
