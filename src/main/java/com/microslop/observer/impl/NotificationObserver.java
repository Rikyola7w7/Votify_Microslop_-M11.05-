package com.microslop.observer.impl;

import com.microslop.event.VoteEvent;
import com.microslop.observer.observer.VoteObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Stub implementation of NotificationObserver.
 * Placeholder for sending notifications on voting events.
 * Can be extended to send emails, push notifications, or SMS alerts.
 *
 * @author Votify Team
 * @version 1.0
 */
@Component
public class NotificationObserver implements VoteObserver {
    
    private static final Logger log = LoggerFactory.getLogger(NotificationObserver.class);
    
    /**
     * Called when a vote is submitted.
     * Can be extended to send notifications to project owner.
     * 
     * @param event the vote submitted event
     */
    @Override
    public void onVoteSubmitted(VoteEvent event) {
        log.debug("Notification: Vote submitted on project {} by {}", 
                 event.getProjectId(), event.getUsername());
        // Future: Send notification to project owner
        // - Email notification
        // - Push notification
        // - SMS alert
    }
    
    /**
     * Called when a vote is undone.
     * Can be extended to send notifications about vote removal.
     * 
     * @param event the vote undone event
     */
    @Override
    public void onVoteUndone(VoteEvent event) {
        log.debug("Notification: Vote undone on project {} by {}", 
                 event.getProjectId(), event.getUsername());
        // Future: Send notification about vote removal
    }
    
    /**
     * Called when a vote is redone.
     * Can be extended to send notifications about vote restoration.
     * 
     * @param event the vote redone event
     */
    @Override
    public void onVoteRedone(VoteEvent event) {
        log.debug("Notification: Vote redone on project {} by {}", 
                 event.getProjectId(), event.getUsername());
        // Future: Send notification about vote restoration
    }
    
    @Override
    public String getObserverName() {
        return "NotificationObserver";
    }
}
