package com.microslop.exception;

import java.util.Collections;
import java.util.List;

public class BusinessValidationException extends RuntimeException {

    private final List<String> validationErrors;

    public BusinessValidationException(String message) {
        super(message);
        this.validationErrors = List.of(message);
    }

    public BusinessValidationException(List<String> validationErrors) {
        super("Validation failed: " + String.join("; ", validationErrors));
        this.validationErrors = Collections.unmodifiableList(validationErrors);
    }

    public BusinessValidationException(String field, String message) {
        super(message);
        this.validationErrors = List.of(message);
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }
}
