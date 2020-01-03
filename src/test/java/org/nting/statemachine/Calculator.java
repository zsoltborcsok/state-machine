package org.nting.statemachine;

import static org.nting.statemachine.Calculator.CalculatorCharacterSignal.NUMBER;
import static org.nting.statemachine.Calculator.CalculatorCharacterSignal.OPERATION;
import static org.nting.statemachine.Calculator.CalculatorSignal.CLEAR;
import static org.nting.statemachine.Calculator.CalculatorSignal.EQUALS;
import static org.nting.statemachine.Calculator.CalculatorSignal.NEGATE;
import static org.nting.statemachine.Calculator.CalculatorSignal.OFF;
import static org.nting.statemachine.StateMachineSignal.ENTRY;
import static org.nting.statemachine.StateMachineSignal.INIT;

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Calculator {

    private static final String CHARACTER_KEY = "character";

    public enum CalculatorSignal implements IEventSignal {
        NEGATE, EQUALS, CLEAR, OFF
    }

    public enum CalculatorCharacterSignal implements IEventSignal {
        NUMBER(ImmutableList.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.')), //
        OPERATION(ImmutableList.of('+', '-', '*', '/'));

        private final List<Character> validCharacters;

        CalculatorCharacterSignal(List<Character> validCharacters) {
            this.validCharacters = validCharacters;
        }

        public boolean isValidCharacter(Character character) {
            return validCharacters.contains(character);
        }
    }

    private final State topState;
    private final State stateOn;
    private final State stateOff;
    private final State stateOperand1;
    private final State stateOperand2;
    private final State stateOpEntered;
    private final State stateResult;

    private final StateMachine stateMachine;

    private String operand1;
    private String operand2;
    private Character operation;
    public Double result;

    public Calculator() {
        topState = new State(this::topState, "TopState");
        stateOn = new State(this::stateOn, "StateOn");
        stateOff = new State(this::stateOff, "StateOff");
        stateOperand1 = new State(this::stateOperand1, "StateOperand1");
        stateOperand2 = new State(this::stateOperand2, "StateOperand2");
        stateOpEntered = new State(this::stateOpEntered, "StateOpEntered");
        stateResult = new State(this::stateResult, "StateResult");

        stateMachine = new StateMachine(topState);
        stateMachine.initialize();
    }

    public void dispatch(CalculatorSignal signal) {
        stateMachine.dispatch(new StateMachineEvent(signal));
    }

    public void dispatch(CalculatorCharacterSignal signal, Character character) {
        Preconditions.checkArgument(signal.isValidCharacter(character));
        stateMachine.dispatch(new StateMachineEvent(signal, ImmutableMap.of(CHARACTER_KEY, character)));
    }

    private State topState(StateMachineEvent stateMachineEvent) {
        if (stateMachineEvent.getEventSignal() == INIT) {
            stateMachine.transitionTo(stateOn);
        }

        return null;
    }

    private State stateOn(StateMachineEvent stateMachineEvent) {
        if (stateMachineEvent.getEventSignal() == INIT) {
            initializeVariables();
            stateMachine.transitionTo(stateOperand1);
            return null;
        } else if (stateMachineEvent.getEventSignal() == OFF) {
            initializeVariables();
            stateMachine.transitionTo(stateOff);
            return null;
        } else if (stateMachineEvent.getEventSignal() == CLEAR) {
            stateMachine.transitionTo(stateOn);
            return null;
        }

        return topState;
    }

    private State stateOff(StateMachineEvent stateMachineEvent) {
        return topState;
    }

    private State stateOperand1(StateMachineEvent stateMachineEvent) {
        if (stateMachineEvent.getEventSignal() == NUMBER) {
            operand1 = operand1 + stateMachineEvent.getProperty(CHARACTER_KEY);
            stateMachine.transitionTo(stateOperand1);
            return null;
        } else if (stateMachineEvent.getEventSignal() == NEGATE) {
            operand1 = negate(operand1);
            stateMachine.transitionTo(stateOperand1);
            return null;
        } else if (stateMachineEvent.getEventSignal() == OPERATION) {
            operation = (Character) stateMachineEvent.getProperty(CHARACTER_KEY);
            stateMachine.transitionTo(stateOpEntered);
            return null;
        }

        return stateOn;
    }

    private State stateOperand2(StateMachineEvent stateMachineEvent) {
        if (stateMachineEvent.getEventSignal() == NUMBER) {
            operand2 = operand2 + stateMachineEvent.getProperty(CHARACTER_KEY);
            stateMachine.transitionTo(stateOperand2);
            return null;
        } else if (stateMachineEvent.getEventSignal() == NEGATE) {
            operand2 = negate(operand2);
            stateMachine.transitionTo(stateOperand2);
            return null;
        } else if (stateMachineEvent.getEventSignal() == EQUALS) {
            stateMachine.transitionTo(stateResult);
            return null;
        }

        return stateOn;
    }

    private State stateOpEntered(StateMachineEvent stateMachineEvent) {
        if (stateMachineEvent.getEventSignal() == NUMBER) {
            operand2 = operand2 + stateMachineEvent.getProperty(CHARACTER_KEY);
            stateMachine.transitionTo(stateOperand2);
            return null;
        } else if (stateMachineEvent.getEventSignal() == NEGATE) {
            operand2 = negate(operand2);
            stateMachine.transitionTo(stateOperand2);
            return null;
        }

        return stateOn;
    }

    private State stateResult(StateMachineEvent stateMachineEvent) {
        if (stateMachineEvent.getEventSignal() == ENTRY) {
            double operand1 = Double.parseDouble(this.operand1);
            double operand2 = Double.parseDouble(this.operand2);
            switch (operation) {
            case '+':
                result = operand1 + operand2;
                break;
            case '-':
                result = operand1 - operand2;
                break;
            case '*':
                result = operand1 * operand2;
                break;
            case '/':
                result = operand1 / operand2;
                break;
            }
            return null;
        } else if (stateMachineEvent.getEventSignal() == NUMBER) {
            initializeVariables();
            operand1 = operand1 + stateMachineEvent.getProperty(CHARACTER_KEY);
            stateMachine.transitionTo(stateOperand1);
            return null;
        } else if (stateMachineEvent.getEventSignal() == NEGATE) {
            initializeVariables();
            stateMachine.transitionTo(stateOperand1);
            return null;
        } else if (stateMachineEvent.getEventSignal() == OPERATION) {
            operand1 = result.toString();
            operand2 = "";
            operation = (Character) stateMachineEvent.getProperty(CHARACTER_KEY);
            result = null;
            stateMachine.transitionTo(stateOpEntered);
            return null;
        }

        return stateOn;
    }

    private void initializeVariables() {
        operand1 = "";
        operand2 = "";
        operation = null;
        result = null;
    }

    private String negate(String operand) {
        return operand.startsWith("-") ? operand.substring(1) : "-" + operand;
    }

}
