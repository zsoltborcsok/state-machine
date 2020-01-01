package org.nting.statemachine;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class StateMachine {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final State topState;
    private State currentState;
    private State sourceState;
    private final Map<State, State> historyStates = Maps.newHashMap();

    public StateMachine(State topState) {
        this.topState = topState;
        currentState = topState;
        sourceState = topState;
    }

    public void initialize() {
        trigger(topState, StateMachineSignal.INIT);
    }

    public void dispatch(StateMachineEvent stateMachineEvent) {
        logger.info("Event: {}", stateMachineEvent);

        sourceState = currentState;
        while (sourceState != null) {
            sourceState = sourceState.stateHandler.handle(stateMachineEvent);
        }
    }

    // TODO(?) we need to propagate the event properties to the targetState as well
    public void transitionTo(State targetState) {
        Preconditions.checkArgument(targetState != topState);

        if (getParentState(targetState) != currentState) {// initial transitions shouldn't trigger Exit events!
            doExitUpToSourceState();
        }
        doTransitionTo(targetState);
        doActivateTargetState(targetState);
    }

    public void transitionToHistoryOf(State targetState, boolean isDeepHistory) {
        Preconditions.checkArgument(targetState != topState);

        doExitUpToSourceState();
        doTransitionTo(targetState);
        doActivateTargetStateForHistory(targetState, isDeepHistory);
    }

    private void doExitUpToSourceState() {
        State state = currentState;
        while (state != sourceState) {
            Preconditions.checkNotNull(state);

            trigger(state, StateMachineSignal.EXIT);
            state = getParentState(state);
        }
    }

    private void doTransitionTo(State targetState) {
        // NOTE: Dynamic version of calling exit and entry events.
        if (sourceState == targetState) {// transition to self
            trigger(sourceState, StateMachineSignal.EXIT);
            trigger(sourceState, StateMachineSignal.ENTRY);
        } else if (getParentState(sourceState) == getParentState(targetState)) {// same level (most common)
            trigger(sourceState, StateMachineSignal.EXIT);
            trigger(targetState, StateMachineSignal.ENTRY);
        } else if (currentState == getParentState(targetState)) {// initial transition
            trigger(targetState, StateMachineSignal.ENTRY);
        } else {// different level
            List<State> parentsOfSourceState = Lists.newLinkedList();
            State state = sourceState;
            while (state != topState) {
                parentsOfSourceState.add(0, state);
                state = getParentState(state);
            }

            List<State> parentsOfTargetState = Lists.newLinkedList();
            state = targetState;
            while (state != topState) {
                parentsOfTargetState.add(0, state);
                state = getParentState(state);
            }

            int firstNotCommonParentIndex = 0;
            if (parentsOfSourceState.size() > 0) {// initial transition comes from topState, so
                // parentsOfSourceState.size() is 0!
                while (parentsOfSourceState.get(firstNotCommonParentIndex) == parentsOfTargetState
                        .get(firstNotCommonParentIndex)) {
                    firstNotCommonParentIndex++;
                }
            }
            for (int i = parentsOfSourceState.size() - 1; i >= firstNotCommonParentIndex; i--) {
                trigger(parentsOfSourceState.get(i), StateMachineSignal.EXIT);
            }
            for (int i = firstNotCommonParentIndex; i < parentsOfTargetState.size(); i++) {
                trigger(parentsOfTargetState.get(i), StateMachineSignal.ENTRY);
            }
        }
    }

    private void doActivateTargetState(State targetState) {
        currentState = targetState;
        sourceState = currentState;
        trigger(currentState, StateMachineSignal.INIT);
    }

    private void doActivateTargetStateForHistory(State targetState, boolean isDeepHistory) {
        while (historyStates.containsKey(targetState)) {
            targetState = historyStates.get(targetState);
            trigger(targetState, StateMachineSignal.ENTRY);
            if (!isDeepHistory) {
                break;
            }
        }

        doActivateTargetState(targetState);
    }

    private State getParentState(State state) {
        return state.stateHandler.handle(new StateMachineEvent(StateMachineSignal.EMPTY));
    }

    private void trigger(State state, IEventSignal eventSignal) {
        if (eventSignal == StateMachineSignal.ENTRY && !state.isPseudo) {
            historyStates.put(getParentState(state), state);
        }

        StateMachineEvent stateMachineEvent = new StateMachineEvent(eventSignal);
        logger.info("State: {}, Event: {}, StateHandler: {}", state.stateName, stateMachineEvent, state.stateHandler);
        state.stateHandler.handle(stateMachineEvent);
    }
}
