package com.microslop.observer.observer;

import com.microslop.event.RankingEvent;

/**
 * Observer interface for ranking-related events.
 * Implementations react to ranking updates and project score changes.
 * This interface defines the contract for any component that needs to respond to ranking activity.
 *
 * @author Votify Team
 * @version 1.0
 */
public interface RankingObserver {
    
    /**
     * Called when project rankings are updated.
     * This method is invoked when rankings for a competition are recalculated.
     * 
     * @param event the ranking updated event
     */
    void onRankingUpdated(RankingEvent event);
    
    /**
     * Called when a project's score changes.
     * This method is invoked when a project's score is updated.
     * 
     * @param event the project score changed event
     */
    void onProjectScoreChanged(RankingEvent event);
    
    /**
     * Gets the observer name for logging and debugging.
     * 
     * @return observer name
     */
    String getObserverName();
}
