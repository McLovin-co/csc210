package com.example.math;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class BooleanLogicTest {

    @Test
    void andTrueTrue() { assertTrue(BooleanLogic.And(true, true)); }

    @Test
    void andTrueFalse() { assertFalse(BooleanLogic.And(true, false)); }

    @Test
    void andFalseFalse() { assertFalse(BooleanLogic.And(false, false)); }

    @Test
    void orTrueTrue() { assertTrue(BooleanLogic.Or(true, true)); }

    @Test
    void orTrueFalse() { assertTrue(BooleanLogic.Or(true, false)); }

    @Test
    void orFalseFalse() { assertFalse(BooleanLogic.Or(false, false)); }

    @Test
    void notTrue() { assertFalse(BooleanLogic.Not(true)); }

    @Test
    void notFalse() { assertTrue(BooleanLogic.Not(false)); }

    @Test
    void xorTrueTrue() { assertFalse(BooleanLogic.Xor(true, true)); }

    @Test
    void xorTrueFalse() { assertTrue(BooleanLogic.Xor(true, false)); }

    @Test
    void xorFalseFalse() { assertFalse(BooleanLogic.Xor(false, false)); }
}
