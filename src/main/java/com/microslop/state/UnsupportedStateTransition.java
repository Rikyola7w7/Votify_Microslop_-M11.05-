package com.microslop.state;

import com.microslop.exception.CompetitionStateException;

public class UnsupportedStateTransition extends CompetitionStateException {

    private final String fromState;
    private final String toState;

    public UnsupportedStateTransition(String fromState, String toState) {
        super(String.format("Cannot transition from '%s' to '%s'", fromState, toState));
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
