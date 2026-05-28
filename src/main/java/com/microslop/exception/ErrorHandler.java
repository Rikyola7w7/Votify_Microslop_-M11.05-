package com.microslop.exception;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized error handler for Vaadin views.
 * Provides consistent error notification display and logging.
 */
public final class ErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);
    private static final int ERROR_DURATION = 4000;
    private static final int WARNING_DURATION = 3000;
    private static final int SUCCESS_DURATION = 3000;

    private ErrorHandler() {
    }

    /**
     * Handles an exception by showing a user-friendly notification and logging the technical details.
     */
    public static void handleException(Exception exception, String context) {
        log.error("Error in {}: {}", context, exception.getMessage(), exception);

        ErrorMessageService messageService = new ErrorMessageService();
        String friendlyMessage = messageService.getFriendlyMessage(exception);

        showErrorNotification(friendlyMessage);
    }

    /**
     * Handles an exception with a specific user-friendly message override.
     */
    public static void handleException(Exception exception, String context, String userMessage) {
        log.error("Error in {}: {}", context, exception.getMessage(), exception);
        showErrorNotification(userMessage);
    }

    /**
     * Shows an error notification to the user.
     */
    public static void showErrorNotification(String message) {
        Notification notification = new Notification(message, ERROR_DURATION, Notification.Position.BOTTOM_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.open();
    }

    /**
     * Shows a warning notification to the user.
     */
    public static void showWarningNotification(String message) {
        Notification notification = new Notification(message, WARNING_DURATION, Notification.Position.BOTTOM_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
        notification.open();
    }

    /**
     * Shows a success notification to the user.
     */
    public static void showSuccessNotification(String message) {
        Notification notification = new Notification(message, SUCCESS_DURATION, Notification.Position.BOTTOM_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.open();
    }

    /**
     * Shows an info notification to the user.
     */
    public static void showInfoNotification(String message) {
        Notification notification = new Notification(message, WARNING_DURATION, Notification.Position.BOTTOM_CENTER);
        notification.open();
    }

    /**
     * Shows an error notification with a custom duration and position.
     */
    public static void showErrorNotification(String message, int durationMs, Notification.Position position) {
        Notification notification = new Notification(message, durationMs, position);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.open();
    }
}
