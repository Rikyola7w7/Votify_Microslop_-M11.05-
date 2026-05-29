package com.microslop.event;

import com.microslop.entity.Competition;

/**
 * Event fired when a competition is concluded.
 * Indicates that the competition has ended and voting is no longer allowed.
 *
 * @author Votify Team
 * @version 1.0
 */
public class CompetitionConcludedEvent extends CompetitionEvent {
    
    /**
     * Creates a new CompetitionConcludedEvent.
     * 
     * @param competition the competition that was concluded
     * @throws NullPointerException if competition is null
     */
    public CompetitionConcludedEvent(Competition competition) {
        super(competition);
    }
    
    @Override
    public String getEventType() {
        return "COMPETITION_CONCLUDED";
    }
}
