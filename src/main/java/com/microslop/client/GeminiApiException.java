package com.microslop.client;

import com.microslop.exception.ExternalServiceException;

/**
 * Exception thrown when the Gemini API returns an error.
 * Kept for backward compatibility. New code should use ExternalServiceException.
 */
public class GeminiApiException extends ExternalServiceException {

    public GeminiApiException(String message, int statusCode) {
        super("Gemini", message, statusCode);
    }

    public GeminiApiException(String message, Throwable cause) {
        super("Gemini", message, cause);
    }
}
