package dev.ahwz.ipd.model;

import static org.junit.jupiter.api.Assertions.*;

class ActionTest {

    @org.junit.jupiter.api.Test
    void test() {
        assertEquals(Action.COOPERATE.opposite(), Action.DEFECT);
        assertEquals(Action.DEFECT.opposite(), Action.COOPERATE);
    }

}