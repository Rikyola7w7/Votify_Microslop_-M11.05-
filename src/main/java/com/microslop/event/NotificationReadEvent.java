package com.microslop.event;

import com.microslop.entity.Notification;

/**
 * Event triggered when a notification is marked as read.
 * This event is published when a notification's read status is updated.
 *
 * @author Votify Team
 * @version 1.0
 */
public class NotificationReadEvent extends NotificationEvent {
    
    /**
     * Creates a new notification read event.
     * 
     * @param notification the notification that was marked as read
     * @param username the username of the notification recipient
     */
    public NotificationReadEvent(Notification notification, String username) {
        super(notification, username);
    }
    
    @Override
    public String getEventType() {
        return "NOTIFICATION_READ";
    }
}
