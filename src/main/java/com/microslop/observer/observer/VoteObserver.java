package com.microslop.observer.observer;

import com.microslop.event.VoteEvent;

/**
 * Observer interface for vote-related events.
 * Implementations react to vote submissions, undos, and redos.
 * This interface defines the contract for any component that needs to respond to voting activity.
 *
 * @author Votify Team
 * @version 1.0
 */
public interface VoteObserver {
    
    /**
     * Called when a vote is submitted.
     * This method is invoked after a vote has been successfully persisted.
     * Implementations should handle any side effects needed when votes are submitted.
     * 
     * @param event the vote submitted event containing vote details
     */
    void onVoteSubmitted(VoteEvent event);
    
    /**
     * Called when a vote is undone.
     * This method is invoked when a vote is removed or reverted.
     * 
     * @param event the vote undone event
     */
    void onVoteUndone(VoteEvent event);
    
    /**
     * Called when a vote is redone.
     * This method is invoked when a previously undone vote is restored.
     * 
     * @param event the vote redone event
     */
    void onVoteRedone(VoteEvent event);
    
    /**
     * Gets the observer name for logging and debugging.
     * Used for identifying which observer produced log messages.
     * 
     * @return observer name
     */
    String getObserverName();
}
