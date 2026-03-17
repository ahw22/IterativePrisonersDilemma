package dev.ahwz.ipd.model;

public record RoundResult(Action moveA, Action moveB, int payoffA, int payoffB, boolean noisyA, boolean noisyB) {
}
