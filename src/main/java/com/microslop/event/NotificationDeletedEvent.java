package com.microslop.event;

import com.microslop.entity.Notification;

/**
 * Event triggered when a notification is deleted.
 * This event is published when a notification is removed from the database.
 *
 * @author Votify Team
 * @version 1.0
 */
public class NotificationDeletedEvent extends NotificationEvent {
    
    /**
     * Creates a new notification deleted event.
     * 
     * @param notification the notification that was deleted
     * @param username the username of the notification recipient
     */
    public NotificationDeletedEvent(Notification notification, String username) {
        super(notification, username);
    }
    
    @Override
    public String getEventType() {
        return "NOTIFICATION_DELETED";
    }
}
