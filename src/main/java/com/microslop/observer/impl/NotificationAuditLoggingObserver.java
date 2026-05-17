package com.microslop.observer.impl;

import com.microslop.event.NotificationCreatedEvent;
import com.microslop.event.NotificationReadEvent;
import com.microslop.event.NotificationDeletedEvent;
import com.microslop.observer.observer.NotificationEventObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Audit logging observer for notification events.
 * Logs all notification-related activities for audit trail and compliance purposes.
 * This observer tracks notification creation, reading, and deletion events.
 *
 * @author Votify Team
 * @version 1.0
 */
@Component
public class NotificationAuditLoggingObserver implements NotificationEventObserver {
    
    private static final Logger log = LoggerFactory.getLogger(NotificationAuditLoggingObserver.class);
    
    @Override
    public void onNotificationCreated(NotificationCreatedEvent event) {
        log.info("AUDIT: Notification created - ID: {}, User: {}, Type: {}, Title: {}",
            event.getNotificationId(),
            event.getUsername(),
            event.getType(),
            event.getTitle());
    }
    
    @Override
    public void onNotificationRead(NotificationReadEvent event) {
        log.info("AUDIT: Notification marked as read - ID: {}, User: {}, Type: {}",
            event.getNotificationId(),
            event.getUsername(),
            event.getType());
    }
    
    @Override
    public void onNotificationDeleted(NotificationDeletedEvent event) {
        log.info("AUDIT: Notification deleted - ID: {}, User: {}, Type: {}, Title: {}",
            event.getNotificationId(),
            event.getUsername(),
            event.getType(),
            event.getTitle());
    }
    
    @Override
    public String getObserverName() {
        return "NotificationAuditLoggingObserver";
    }
}
