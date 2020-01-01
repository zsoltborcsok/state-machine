package org.nting.statemachine;

import static org.nting.statemachine.StateMachineSignal.INIT;
import static org.nting.statemachine.StateMachineTest.KeySignal.KEY_1;
import static org.nting.statemachine.StateMachineTest.KeySignal.KEY_2;
import static org.nting.statemachine.StateMachineTest.KeySignal.KEY_3;
import static org.nting.statemachine.StateMachineTest.KeySignal.KEY_4;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

public class StateMachineTest {

    public enum KeySignal implements IEventSignal {
        KEY_1, KEY_2, KEY_3, KEY_4
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private StateMachine stateMachine;

    private State topState;
    private State state1;
    private State state11;
    private State state111;
    private State state112;
    private State state2;
    private State state21;
    private State state211;
    private State state212;
    private State cp1;// A Condition Point pseudo state.
    private State bp1;// A Branch Point pseudo state.

    @Before
    public void setUp() {
        topState = new State(this::topState, "TopState");

        state1 = new State(this::state1, "State1");
        state11 = new State(this::state11, "State11");
        state111 = new State(this::state111, "State111");
        state112 = new State(this::state112, "State112");

        state2 = new State(this::state2, "State2");
        state21 = new State(this::state21, "State21");
        state211 = new State(this::state211, "State211");
        state212 = new State(this::state212, "State212");

        cp1 = new State(this::cp1, "CP1", true);

        bp1 = new State(this::bp1, "BP1", true);
        stateMachine = new StateMachine(topState);
    }

    private State topState(StateMachineEvent stateMachineEvent) {
        if (stateMachineEvent.getEventSignal() == INIT) {
            stateMachine.transitionTo(state1);
        }

        return null;
    }

    private State state1(StateMachineEvent stateMachineEvent) {
        if (stateMachineEvent.getEventSignal() == KEY_2) {
            stateMachine.transitionToHistoryOf(state2, false);
            return null;
        } else if (stateMachineEvent.getEventSignal() == INIT) {
            stateMachine.transitionTo(state11);
            return null;
        }

        return topState;
    }

    private State state11(StateMachineEvent stateMachineEvent) {
        if (stateMachineEvent.getEventSignal() == INIT) {
            stateMachine.transitionTo(state111);
            return null;
        }

        return state1;
    }

    private State state111(StateMachineEvent stateMachineEvent) {
        if (stateMachineEvent.getEventSignal() == KEY_4) {
            stateMachine.transitionTo(state112);
            return null;
        }

        return state11;
    }

    private State state112(StateMachineEvent stateMachineEvent) {
        if (stateMachineEvent.getEventSignal() == KEY_4) {
            stateMachine.transitionTo(cp1);
            return null;
        }

        return state11;
    }

    private State state2(StateMachineEvent stateMachineEvent) {
        if (stateMachineEvent.getEventSignal() == KEY_1) {
            stateMachine.transitionToHistoryOf(state1, true);
            return null;
        } else if (stateMachineEvent.getEventSignal() == INIT) {
            stateMachine.transitionTo(state21);
            return null;
        }

        return topState;
    }

    private State state21(StateMachineEvent stateMachineEvent) {
        if (stateMachineEvent.getEventSignal() == INIT) {
            stateMachine.transitionTo(state211);
            return null;
        }

        return state2;
    }

    private State state211(StateMachineEvent stateMachineEvent) {
        if (stateMachineEvent.getEventSignal() == KEY_3) {
            stateMachine.transitionTo(state212);
            return null;
        }

        return state21;
    }

    private State state212(StateMachineEvent stateMachineEvent) {
        if (stateMachineEvent.getEventSignal() == KEY_3) {
            stateMachine.transitionTo(bp1);
            return null;
        }

        return state21;
    }

    private State cp1(StateMachineEvent stateMachineEvent) {
        if (stateMachineEvent.getEventSignal() == INIT) {
            if (stateMachineEvent.getProperty("count", 0) < 0) {
                stateMachine.transitionTo(state111);
            } else {
                stateMachine.transitionTo(state2);
            }
            return null;
        }

        return state11;
    }

    private State bp1(StateMachineEvent stateMachineEvent) {
        if (INIT.equals(stateMachineEvent.getEventSignal())) {
            switch (stateMachineEvent.getProperty("count", 0)) {
            case 0:
                stateMachine.transitionTo(state212);
                break;
            case 1:
                stateMachine.transitionTo(state211);
                break;
            default:
                stateMachine.transitionTo(state11);
                break;
            }
            return null;
        }

        return state21;
    }

    @Test
    public void testStateMachine() {
        logger.info("[INITIALIZE]");
        stateMachine.initialize();
        logger.info("[HISTORY - DEEP HISTORY POINT TEST]");
        stateMachine.dispatch(new StateMachineEvent(KEY_2));
        stateMachine.dispatch(new StateMachineEvent(KEY_3));
        stateMachine.dispatch(new StateMachineEvent(KEY_1));
        stateMachine.dispatch(new StateMachineEvent(KEY_4));
        stateMachine.dispatch(new StateMachineEvent(KEY_2));
        stateMachine.dispatch(new StateMachineEvent(KEY_3));
        stateMachine.dispatch(new StateMachineEvent(KEY_1));
        logger.info("[CONDITION POINT TEST]");
        stateMachine.dispatch(new StateMachineEvent(KEY_4, ImmutableMap.of("count", -1)));
        stateMachine.dispatch(new StateMachineEvent(KEY_1));
        stateMachine.dispatch(new StateMachineEvent(KEY_4));
        logger.info("[BRANCH POINT TEST]");
        stateMachine.dispatch(new StateMachineEvent(KEY_2));
        stateMachine.dispatch(new StateMachineEvent(KEY_3));
        logger.info("[NOW]");
        stateMachine.dispatch(new StateMachineEvent(KEY_3));
        stateMachine.dispatch(new StateMachineEvent(KEY_3));
        stateMachine.dispatch(new StateMachineEvent(KEY_3));
        stateMachine.dispatch(new StateMachineEvent(KEY_3));
    }

}