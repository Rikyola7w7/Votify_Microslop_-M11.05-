package com.microslop.observer.impl;

import com.microslop.event.CompetitionEvent;
import com.microslop.event.CompetitionActivatedEvent;
import com.microslop.event.CompetitionDeactivatedEvent;
import com.microslop.event.CompetitionConcludedEvent;
import com.microslop.event.CompetitionVotingOpenedEvent;
import com.microslop.event.CompetitionVotingPausedEvent;
import com.microslop.event.CompetitionArchivedEvent;
import com.microslop.event.CompetitionReopenedEvent;
import com.microslop.observer.observer.CompetitionObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CompetitionStateObserver implements CompetitionObserver {
    
    private static final Logger log = LoggerFactory.getLogger(CompetitionStateObserver.class);
    
    @Override
    public void onCompetitionActivated(CompetitionEvent event) {
        if (!(event instanceof CompetitionActivatedEvent)) {
            return;
        }
        try {
            log.info("Competition '{}' (ID: {}) has been activated for voting", 
                    event.getCompetitionName(), event.getCompetitionId());
        } catch (Exception e) {
            log.error("Error handling competition activation", e);
        }
    }
    
    @Override
    public void onCompetitionDeactivated(CompetitionEvent event) {
        if (!(event instanceof CompetitionDeactivatedEvent)) {
            return;
        }
        try {
            log.info("Competition '{}' (ID: {}) has been deactivated", 
                    event.getCompetitionName(), event.getCompetitionId());
        } catch (Exception e) {
            log.error("Error handling competition deactivation", e);
        }
    }
    
    @Override
    public void onCompetitionConcluded(CompetitionEvent event) {
        if (!(event instanceof CompetitionConcludedEvent)) {
            return;
        }
        try {
            log.info("Competition '{}' (ID: {}) has been concluded", 
                    event.getCompetitionName(), event.getCompetitionId());
        } catch (Exception e) {
            log.error("Error handling competition conclusion", e);
        }
    }

    @Override
    public void onVotingOpened(CompetitionEvent event) {
        if (!(event instanceof CompetitionVotingOpenedEvent)) {
            return;
        }
        try {
            log.info("Competition '{}' (ID: {}) voting has been opened",
                    event.getCompetitionName(), event.getCompetitionId());
        } catch (Exception e) {
            log.error("Error handling voting opened", e);
        }
    }

    @Override
    public void onVotingPaused(CompetitionEvent event) {
        if (!(event instanceof CompetitionVotingPausedEvent)) {
            return;
        }
        try {
            log.info("Competition '{}' (ID: {}) voting has been paused",
                    event.getCompetitionName(), event.getCompetitionId());
        } catch (Exception e) {
            log.error("Error handling voting paused", e);
        }
    }

    @Override
    public void onCompetitionArchived(CompetitionEvent event) {
        if (!(event instanceof CompetitionArchivedEvent)) {
            return;
        }
        try {
            log.info("Competition '{}' (ID: {}) has been archived",
                    event.getCompetitionName(), event.getCompetitionId());
        } catch (Exception e) {
            log.error("Error handling competition archived", e);
        }
    }

    @Override
    public void onCompetitionReopened(CompetitionEvent event) {
        if (!(event instanceof CompetitionReopenedEvent)) {
            return;
        }
        try {
            log.info("Competition '{}' (ID: {}) has been reopened",
                    event.getCompetitionName(), event.getCompetitionId());
        } catch (Exception e) {
            log.error("Error handling competition reopened", e);
        }
    }
    
    @Override
    public String getObserverName() {
        return "CompetitionStateObserver";
    }
}
