package com.microslop.event;

import com.microslop.entity.Notification;

/**
 * Event triggered when a new notification is created.
 * This event is published when a notification is successfully persisted to the database.
 *
 * @author Votify Team
 * @version 1.0
 */
public class NotificationCreatedEvent extends NotificationEvent {
    
    /**
     * Creates a new notification created event.
     * 
     * @param notification the created notification entity
     * @param username the username of the notification recipient
     */
    public NotificationCreatedEvent(Notification notification, String username) {
        super(notification, username);
    }
    
    @Override
    public String getEventType() {
        return "NOTIFICATION_CREATED";
    }
}
