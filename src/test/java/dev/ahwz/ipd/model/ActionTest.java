package dev.ahwz.ipd.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActionTest {

    @Test
    void cooperateOpposite_returnsDefect() {
        assertEquals(Action.DEFECT, Action.COOPERATE.opposite());
    }

    @Test
    void defectOpposite_returnsCooperate() {
        assertEquals(Action.COOPERATE, Action.DEFECT.opposite());
    }
}
