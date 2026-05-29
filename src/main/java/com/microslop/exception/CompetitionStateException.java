package com.microslop.exception;

public class CompetitionStateException extends RuntimeException {

    private final String currentState;
    private final String attemptedAction;

    public CompetitionStateException(String currentState, String attemptedAction) {
        super("Cannot " + attemptedAction + " while competition is in " + currentState + " state");
        this.currentState = currentState;
        this.attemptedAction = attemptedAction;
    }

    public CompetitionStateException(String message) {
        super(message);
        this.currentState = null;
        this.attemptedAction = null;
    }

    public String getCurrentState() {
        return currentState;
    }

    public String getAttemptedAction() {
        return attemptedAction;
    }
}
