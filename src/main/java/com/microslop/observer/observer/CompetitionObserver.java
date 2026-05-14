package com.microslop.observer.observer;

import com.microslop.event.CompetitionEvent;

/**
 * Observer interface for competition-related events.
 * Implementations react to competition state transitions (activated, deactivated, concluded).
 * This interface defines the contract for any component that needs to respond to competition state changes.
 *
 * @author Votify Team
 * @version 1.0
 */
public interface CompetitionObserver {
    
    /**
     * Called when a competition is activated.
     * This method is invoked when voting is enabled for a competition.
     * 
     * @param event the competition activated event
     */
    void onCompetitionActivated(CompetitionEvent event);
    
    /**
     * Called when a competition is deactivated.
     * This method is invoked when voting is paused or disabled.
     * 
     * @param event the competition deactivated event
     */
    void onCompetitionDeactivated(CompetitionEvent event);
    
    /**
     * Called when a competition is concluded.
     * This method is invoked when a competition ends.
     * 
     * @param event the competition concluded event
     */
    void onCompetitionConcluded(CompetitionEvent event);

    /**
     * Called when voting is opened for a competition.
     * @param event the voting opened event
     */
    default void onVotingOpened(CompetitionEvent event) {}

    /**
     * Called when voting is paused for a competition.
     * @param event the voting paused event
     */
    default void onVotingPaused(CompetitionEvent event) {}

    /**
     * Called when a competition is archived.
     * @param event the archived event
     */
    default void onCompetitionArchived(CompetitionEvent event) {}

    /**
     * Called when a concluded competition is reopened.
     * @param event the reopened event
     */
    default void onCompetitionReopened(CompetitionEvent event) {}

    /**
     * Gets the observer name for logging and debugging.
     * 
     * @return observer name
     */
    String getObserverName();
}
