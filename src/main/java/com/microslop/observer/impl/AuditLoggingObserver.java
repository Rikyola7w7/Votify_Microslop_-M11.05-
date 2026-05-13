package com.microslop.observer.impl;

import com.microslop.event.VoteEvent;
import com.microslop.event.VoteSubmittedEvent;
import com.microslop.event.VoteUndoneEvent;
import com.microslop.event.VoteRedoneEvent;
import com.microslop.observer.observer.VoteObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Observer that logs all vote events for audit trail purposes.
 * Provides comprehensive logging of voting activity for compliance and debugging.
 * Creates a permanent record of all voting actions.
 *
 * @author Votify Team
 * @version 1.0
 */
@Component
public class AuditLoggingObserver implements VoteObserver {
    
    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT");
    
    /**
     * Called when a vote is submitted.
     * Logs the submission for audit trail.
     * 
     * @param event the vote submitted event
     */
    @Override
    public void onVoteSubmitted(VoteEvent event) {
        if (!(event instanceof VoteSubmittedEvent)) {
            return;
        }
        auditLog.info("VOTE_SUBMITTED: User={}, Project={}, Category={}, Timestamp={}", 
                      event.getUsername(), event.getProjectId(), 
                      event.getCategoryId(), event.getTimestamp());
    }
    
    /**
     * Called when a vote is undone.
     * Logs the undo action for audit trail.
     * 
     * @param event the vote undone event
     */
    @Override
    public void onVoteUndone(VoteEvent event) {
        if (!(event instanceof VoteUndoneEvent)) {
            return;
        }
        auditLog.info("VOTE_UNDONE: VoteId={}, User={}, Project={}, Timestamp={}", 
                      event.getVoteId(), event.getUsername(), event.getProjectId(), event.getTimestamp());
    }
    
    /**
     * Called when a vote is redone.
     * Logs the redo action for audit trail.
     * 
     * @param event the vote redone event
     */
    @Override
    public void onVoteRedone(VoteEvent event) {
        if (!(event instanceof VoteRedoneEvent)) {
            return;
        }
        auditLog.info("VOTE_REDONE: VoteId={}, User={}, Project={}, Timestamp={}", 
                      event.getVoteId(), event.getUsername(), event.getProjectId(), event.getTimestamp());
    }
    
    @Override
    public String getObserverName() {
        return "AuditLoggingObserver";
    }
}
