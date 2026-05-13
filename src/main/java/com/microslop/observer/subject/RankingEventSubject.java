package com.microslop.observer.subject;

import com.microslop.event.RankingEvent;
import com.microslop.observer.observer.RankingObserver;

/**
 * Subject interface for ranking-related events.
 * Implementations manage RankingObserver registrations and notifications.
 * This interface defines the contract for observable ranking changes.
 *
 * @author Votify Team
 * @version 1.0
 */
public interface RankingEventSubject {
    
    /**
     * Register an observer to receive ranking events.
     * The observer will be notified of all ranking events until unregistered.
     * 
     * @param observer the observer to register (must not be null)
     */
    void registerRankingObserver(RankingObserver observer);
    
    /**
     * Unregister an observer from ranking events.
     * The observer will no longer receive ranking event notifications.
     * 
     * @param observer the observer to unregister (must not be null)
     */
    void unregisterRankingObserver(RankingObserver observer);
    
    /**
     * Notify all registered observers of a ranking event.
     * Observers are notified sequentially. If an observer throws an exception,
     * it is logged but does not prevent other observers from being notified.
     * 
     * @param event the ranking event to publish (must not be null)
     */
    void notifyRankingObservers(RankingEvent event);
    
    /**
     * Get count of registered observers.
     * 
     * @return number of registered observers
     */
    int getRankingObserverCount();
}
