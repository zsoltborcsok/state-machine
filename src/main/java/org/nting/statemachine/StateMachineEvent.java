package org.nting.statemachine;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.MoreObjects;

public class StateMachineEvent {
    private final IEventSignal eventSignal;
    private final Map<String, Object> properties;

    public StateMachineEvent(IEventSignal eventSignal) {
        this(eventSignal, Collections.emptyMap());
    }

    public StateMachineEvent(IEventSignal eventSignal, Map<String, Object> properties) {
        this.eventSignal = eventSignal;
        this.properties = properties;
    }

    public IEventSignal getEventSignal() {
        return eventSignal;
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key, T defaultValue) {
        return Optional.ofNullable((T) properties.get(key)).orElse(defaultValue);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("eventSignal", eventSignal).add("properties", properties)
                .toString();
    }
}
