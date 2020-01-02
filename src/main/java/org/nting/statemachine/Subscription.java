package org.nting.statemachine;

@FunctionalInterface
public interface Subscription {

    void unsubscribe();
}
