package com.microslop.observer.impl;

import com.microslop.event.CompetitionEvent;
import com.microslop.event.CompetitionActivatedEvent;
import com.microslop.event.CompetitionDeactivatedEvent;
import com.microslop.event.CompetitionConcludedEvent;
import com.microslop.observer.observer.CompetitionObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Observer that handles competition state transitions.
 * Responds to competition activation, deactivation, and conclusion events.
 * Can perform cleanup, notification, or status update operations.
 *
 * @author Votify Team
 * @version 1.0
 */
@Component
public class CompetitionStateObserver implements CompetitionObserver {
    
    private static final Logger log = LoggerFactory.getLogger(CompetitionStateObserver.class);
    
    /**
     * Called when a competition is activated.
     * Enables voting and notifies observers of the state change.
     * 
     * @param event the competition activated event
     */
    @Override
    public void onCompetitionActivated(CompetitionEvent event) {
        if (!(event instanceof CompetitionActivatedEvent)) {
            return;
        }
        try {
            log.info("Competition '{}' (ID: {}) has been activated for voting", 
                    event.getCompetitionName(), event.getCompetitionId());
            // Implementation: Perform activation-related tasks
            // - Notify participants
            // - Reset vote counts if needed
            // - Update UI dashboards
        } catch (Exception e) {
            log.error("Error handling competition activation", e);
        }
    }
    
    /**
     * Called when a competition is deactivated.
     * Disables voting and notifies observers of the state change.
     * 
     * @param event the competition deactivated event
     */
    @Override
    public void onCompetitionDeactivated(CompetitionEvent event) {
        if (!(event instanceof CompetitionDeactivatedEvent)) {
            return;
        }
        try {
            log.info("Competition '{}' (ID: {}) has been deactivated", 
                    event.getCompetitionName(), event.getCompetitionId());
            // Implementation: Perform deactivation-related tasks
            // - Notify judges
            // - Save interim results
            // - Update UI dashboards
        } catch (Exception e) {
            log.error("Error handling competition deactivation", e);
        }
    }
    
    /**
     * Called when a competition is concluded.
     * Finalizes results and notifies observers of completion.
     * 
     * @param event the competition concluded event
     */
    @Override
    public void onCompetitionConcluded(CompetitionEvent event) {
        if (!(event instanceof CompetitionConcludedEvent)) {
            return;
        }
        try {
            log.info("Competition '{}' (ID: {}) has been concluded", 
                    event.getCompetitionName(), event.getCompetitionId());
            // Implementation: Perform conclusion-related tasks
            // - Archive results
            // - Send winner notifications
            // - Generate final reports
        } catch (Exception e) {
            log.error("Error handling competition conclusion", e);
        }
    }
    
    @Override
    public String getObserverName() {
        return "CompetitionStateObserver";
    }
}
