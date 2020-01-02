package org.nting.statemachine;

import com.google.common.base.MoreObjects;

public class State {
    public final IStateHandler stateHandler;
    public final String stateName;
    public final boolean isPseudo;

    public State(IStateHandler stateHandler, String stateName) {
        this(stateHandler, stateName, false);
    }

    public State(IStateHandler stateHandler, String stateName, boolean pseudo) {
        this.stateHandler = stateHandler;
        this.stateName = stateName;
        isPseudo = pseudo;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("stateName", stateName).add("isPseudo", isPseudo)
                .add("stateHandler", stateHandler).toString();
    }
}
