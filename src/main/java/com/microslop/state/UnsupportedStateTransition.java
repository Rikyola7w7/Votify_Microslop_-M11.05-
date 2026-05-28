package com.microslop.state;

public class UnsupportedStateTransition extends IllegalStateException {

    private final String fromState;
    private final String toState;

    public UnsupportedStateTransition(String fromState, String toState) {
        super(String.format("Transition from '%s' to '%s' is not supported", fromState, toState));
        this.fromState = fromState;
        this.toState = toState;
    }

    public String getFromState() {
        return fromState;
    }

    public String getToState() {
        return toState;
    }
}
