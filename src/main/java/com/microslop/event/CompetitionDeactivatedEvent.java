package com.microslop.event;

import com.microslop.entity.Competition;

/**
 * Event fired when a competition is deactivated.
 * Indicates that voting has been paused or temporarily disabled for the competition.
 *
 * @author Votify Team
 * @version 1.0
 */
public class CompetitionDeactivatedEvent extends CompetitionEvent {
    
    /**
     * Creates a new CompetitionDeactivatedEvent.
     * 
     * @param competition the competition that was deactivated
     * @throws NullPointerException if competition is null
     */
    public CompetitionDeactivatedEvent(Competition competition) {
        super(competition);
    }
    
    @Override
    public String getEventType() {
        return "COMPETITION_DEACTIVATED";
    }
}
