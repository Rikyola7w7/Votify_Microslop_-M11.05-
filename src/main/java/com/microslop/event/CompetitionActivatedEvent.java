package com.microslop.event;

import com.microslop.entity.Competition;

/**
 * Event fired when a competition is activated.
 * Indicates that voting has been enabled for the competition.
 *
 * @author Votify Team
 * @version 1.0
 */
public class CompetitionActivatedEvent extends CompetitionEvent {
    
    /**
     * Creates a new CompetitionActivatedEvent.
     * 
     * @param competition the competition that was activated
     * @throws NullPointerException if competition is null
     */
    public CompetitionActivatedEvent(Competition competition) {
        super(competition);
    }
    
    @Override
    public String getEventType() {
        return "COMPETITION_ACTIVATED";
    }
}
