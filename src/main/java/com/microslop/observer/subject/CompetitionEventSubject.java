package com.microslop.observer.subject;

import com.microslop.event.CompetitionEvent;
import com.microslop.observer.observer.CompetitionObserver;

/**
 * Subject interface for competition-related events.
 * Implementations manage CompetitionObserver registrations and notifications.
 * This interface defines the contract for observable competition state changes.
 *
 * @author Votify Team
 * @version 1.0
 */
public interface CompetitionEventSubject {
    
    /**
     * Register an observer to receive competition events.
     * The observer will be notified of all competition events until unregistered.
     * 
     * @param observer the observer to register (must not be null)
     */
    void registerCompetitionObserver(CompetitionObserver observer);
    
    /**
     * Unregister an observer from competition events.
     * The observer will no longer receive competition event notifications.
     * 
     * @param observer the observer to unregister (must not be null)
     */
    void unregisterCompetitionObserver(CompetitionObserver observer);
    
    /**
     * Notify all registered observers of a competition event.
     * Observers are notified sequentially. If an observer throws an exception,
     * it is logged but does not prevent other observers from being notified.
     * 
     * @param event the competition event to publish (must not be null)
     */
    void notifyCompetitionObservers(CompetitionEvent event);
    
    /**
     * Get count of registered observers.
     * 
     * @return number of registered observers
     */
    int getCompetitionObserverCount();
}
