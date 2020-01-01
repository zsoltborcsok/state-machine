package org.nting.statemachine;

public enum StateMachineSignal implements IEventSignal {
    // Get parent state.
    EMPTY,
    // If initial point exists: Perform actions of initial transitions and activate initial state.
    INIT,
    // Perform entry actions.
    ENTRY,
    // Perform exit actions.
    EXIT
}
