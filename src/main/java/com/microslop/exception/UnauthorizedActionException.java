package com.microslop.exception;

public class UnauthorizedActionException extends RuntimeException {

    public UnauthorizedActionException(String message) {
        super(message);
    }

    public UnauthorizedActionException(String action, String reason) {
        super("Cannot " + action + ": " + reason);
    }
}
