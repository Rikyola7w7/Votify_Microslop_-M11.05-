package com.microslop.observer.subject;

import com.microslop.event.VoteEvent;
import com.microslop.observer.observer.VoteObserver;

/**
 * Subject interface for vote-related events.
 * Implementations manage VoteObserver registrations and notifications.
 * This interface defines the contract for observable vote-related operations.
 *
 * @author Votify Team
 * @version 1.0
 */
public interface VoteEventSubject {
    
    /**
     * Register an observer to receive vote events.
     * The observer will be notified of all vote events until unregistered.
     * 
     * @param observer the observer to register (must not be null)
     */
    void registerVoteObserver(VoteObserver observer);
    
    /**
     * Unregister an observer from vote events.
     * The observer will no longer receive vote event notifications.
     * 
     * @param observer the observer to unregister (must not be null)
     */
    void unregisterVoteObserver(VoteObserver observer);
    
    /**
     * Notify all registered observers of a vote event.
     * Observers are notified sequentially. If an observer throws an exception,
     * it is logged but does not prevent other observers from being notified.
     * This ensures robustness and prevents one faulty observer from blocking others.
     * 
     * @param event the vote event to publish (must not be null)
     */
    void notifyVoteObservers(VoteEvent event);
    
    /**
     * Get count of registered observers.
     * Useful for debugging and testing observer registration.
     * 
     * @return number of registered observers
     */
    int getVoteObserverCount();
}
