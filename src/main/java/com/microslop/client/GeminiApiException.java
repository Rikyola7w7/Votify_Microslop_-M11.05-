package com.microslop.client;

/**
 * Exception thrown when the Gemini API returns an error.
 */
public class GeminiApiException extends RuntimeException {

    private final int statusCode;

    public GeminiApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public GeminiApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
