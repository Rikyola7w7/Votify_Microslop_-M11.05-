package com.microslop.observer.observer;

import com.microslop.event.NotificationCreatedEvent;
import com.microslop.event.NotificationReadEvent;
import com.microslop.event.NotificationDeletedEvent;

/**
 * Observer interface for notification-related events.
 * Implementations react to notification creation, reading, and deletion.
 * This interface defines the contract for any component that needs to respond to notification activity.
 *
 * @author Votify Team
 * @version 1.0
 */
public interface NotificationEventObserver {
    
    /**
     * Called when a notification is created.
     * This method is invoked after a notification has been successfully persisted.
     * Implementations should handle any side effects needed when notifications are created.
     * Examples: send email, log audit event, update UI counters
     * 
     * @param event the notification created event containing notification details
     */
    void onNotificationCreated(NotificationCreatedEvent event);
    
    /**
     * Called when a notification is marked as read.
     * This method is invoked when a notification's read status is updated.
     * Implementations can use this to update UI, analytics, or trigger workflows.
     * 
     * @param event the notification read event
     */
    void onNotificationRead(NotificationReadEvent event);
    
    /**
     * Called when a notification is deleted.
     * This method is invoked when a notification is removed from the database.
     * Implementations can use this for cleanup, analytics, or audit logging.
     * 
     * @param event the notification deleted event
     */
    void onNotificationDeleted(NotificationDeletedEvent event);
    
    /**
     * Gets the observer name for logging and debugging.
     * Used for identifying which observer produced log messages.
     * 
     * @return observer name
     */
    String getObserverName();
}
