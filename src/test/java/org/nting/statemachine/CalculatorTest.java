package org.nting.statemachine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.nting.statemachine.Calculator.CalculatorCharacterSignal.NUMBER;
import static org.nting.statemachine.Calculator.CalculatorCharacterSignal.OPERATION;
import static org.nting.statemachine.Calculator.CalculatorSignal.CLEAR;
import static org.nting.statemachine.Calculator.CalculatorSignal.EQUALS;
import static org.nting.statemachine.Calculator.CalculatorSignal.NEGATE;
import static org.nting.statemachine.Calculator.CalculatorSignal.OFF;

import org.junit.Before;
import org.junit.Test;

public class CalculatorTest {

    private Calculator calculator;

    @Before
    public void setUp() {
        calculator = new Calculator();
    }

    @Test
    public void testAdd() {
        calculator.dispatch(NUMBER, '1');
        calculator.dispatch(NUMBER, '5');
        calculator.dispatch(OPERATION, '+');
        calculator.dispatch(NUMBER, '2');
        calculator.dispatch(NUMBER, '7');
        calculator.dispatch(EQUALS);

        assertEquals(42.0, calculator.result, 0.0);
    }

    @Test
    public void testRemove() {
        calculator.dispatch(NUMBER, '1');
        calculator.dispatch(NUMBER, '5');
        calculator.dispatch(OPERATION, '-');
        calculator.dispatch(NUMBER, '2');
        calculator.dispatch(NUMBER, '7');
        calculator.dispatch(EQUALS);

        assertEquals(-12.0, calculator.result, 0.0);
    }

    @Test
    public void testMultiply() {
        calculator.dispatch(NUMBER, '1');
        calculator.dispatch(NUMBER, '5');
        calculator.dispatch(OPERATION, '*');
        calculator.dispatch(NUMBER, '1');
        calculator.dispatch(NUMBER, '0');
        calculator.dispatch(NUMBER, '1');
        calculator.dispatch(NEGATE);
        calculator.dispatch(EQUALS);

        assertEquals(-1515.0, calculator.result, 0.0);
    }

    @Test
    public void testDivision() {
        calculator.dispatch(NUMBER, '1');
        calculator.dispatch(NUMBER, '5');
        calculator.dispatch(OPERATION, '/');
        calculator.dispatch(NUMBER, '4');
        calculator.dispatch(NUMBER, '5');
        calculator.dispatch(EQUALS);

        assertEquals(1.0 / 3.0, calculator.result, 0.0);
    }

    @Test
    public void testNegate() {
        calculator.dispatch(NEGATE);
        calculator.dispatch(NUMBER, '1');
        calculator.dispatch(NUMBER, '5');
        calculator.dispatch(OPERATION, '+');
        calculator.dispatch(NUMBER, '2');
        calculator.dispatch(NEGATE);
        calculator.dispatch(NUMBER, '7');
        calculator.dispatch(NEGATE);
        calculator.dispatch(EQUALS);

        assertEquals(12.0, calculator.result, 0.0);
    }

    @Test
    public void testOff() {
        calculator.dispatch(NUMBER, '1');
        calculator.dispatch(OPERATION, '+');
        calculator.dispatch(NUMBER, '1');
        calculator.dispatch(EQUALS);

        calculator.dispatch(OFF);
        assertNull(calculator.result);

        calculator.dispatch(NUMBER, '1');
        calculator.dispatch(NUMBER, '5');
        calculator.dispatch(OPERATION, '+');
        calculator.dispatch(NUMBER, '2');
        calculator.dispatch(NUMBER, '7');
        calculator.dispatch(EQUALS);

        assertNull(calculator.result);
    }

    @Test
    public void testClear() {
        calculator.dispatch(NUMBER, '1');
        calculator.dispatch(OPERATION, '+');
        calculator.dispatch(NUMBER, '1');

        calculator.dispatch(CLEAR);

        calculator.dispatch(NUMBER, '1');
        calculator.dispatch(NUMBER, '5');
        calculator.dispatch(OPERATION, '+');
        calculator.dispatch(NUMBER, '2');
        calculator.dispatch(NUMBER, '7');
        calculator.dispatch(EQUALS);

        assertEquals(42.0, calculator.result, 0.0);
    }

    @Test
    public void testChaining() {
        calculator.dispatch(NUMBER, '1');
        calculator.dispatch(NUMBER, '5');
        calculator.dispatch(OPERATION, '+');
        calculator.dispatch(NUMBER, '2');
        calculator.dispatch(NUMBER, '7');
        calculator.dispatch(EQUALS);

        calculator.dispatch(OPERATION, '/');
        calculator.dispatch(NUMBER, '2');
        calculator.dispatch(NUMBER, '1');
        calculator.dispatch(EQUALS);

        calculator.dispatch(OPERATION, '*');
        calculator.dispatch(NUMBER, '5');
        calculator.dispatch(NEGATE);
        calculator.dispatch(EQUALS);

        assertEquals(-10.0, calculator.result, 0.0);
    }
}
