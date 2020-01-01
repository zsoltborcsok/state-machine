package org.nting.statemachine;

@FunctionalInterface
public interface IStateHandler {
    State handle(StateMachineEvent event);
}
