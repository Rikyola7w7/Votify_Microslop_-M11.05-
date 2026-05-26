package com.microslop.observer.subject;

import com.microslop.event.NotificationCreatedEvent;
import com.microslop.event.NotificationReadEvent;
import com.microslop.event.NotificationDeletedEvent;
import com.microslop.observer.observer.NotificationEventObserver;

/**
 * Subject interface for notification-related events.
 * Implementations manage NotificationEventObserver registrations and notifications.
 * This interface defines the contract for observable notification-related operations.
 *
 * @author Votify Team
 * @version 1.0
 */
public interface NotificationEventSubject {
    
    /**
     * Register an observer to receive notification events.
     * The observer will be notified of all notification events until unregistered.
     * 
     * @param observer the observer to register (must not be null)
     */
    void registerNotificationObserver(NotificationEventObserver observer);
    
    /**
     * Unregister an observer from notification events.
     * The observer will no longer receive notification event notifications.
     * 
     * @param observer the observer to unregister (must not be null)
     */
    void unregisterNotificationObserver(NotificationEventObserver observer);
    
    /**
     * Notify all registered observers of a notification created event.
     * Observers are notified sequentially. If an observer throws an exception,
     * it is logged but does not prevent other observers from being notified.
     * This ensures robustness and prevents one faulty observer from blocking others.
     * 
     * @param event the notification created event to publish (must not be null)
     */
    void notifyNotificationCreated(NotificationCreatedEvent event);
    
    /**
     * Notify all registered observers of a notification read event.
     * Observers are notified sequentially. If an observer throws an exception,
     * it is logged but does not prevent other observers from being notified.
     * 
     * @param event the notification read event to publish (must not be null)
     */
    void notifyNotificationRead(NotificationReadEvent event);
    
    /**
     * Notify all registered observers of a notification deleted event.
     * Observers are notified sequentially. If an observer throws an exception,
     * it is logged but does not prevent other observers from being notified.
     * 
     * @param event the notification deleted event to publish (must not be null)
     */
    void notifyNotificationDeleted(NotificationDeletedEvent event);
}
