package com.microslop.event;

import com.microslop.entity.Competition;

/**
 * Abstract base class for competition-related events.
 * Provides common properties for all competition events (activated, deactivated, concluded).
 * All competition events carry information about the competition and its state change.
 *
 * @author Votify Team
 * @version 1.0
 */
public abstract class CompetitionEvent extends VotifyEvent {
    
    private final Long competitionId;
    private final String competitionName;
    private final boolean isActive;
    
    /**
     * Creates a new CompetitionEvent from a Competition entity.
     * 
     * @param competition the competition entity
     * @throws NullPointerException if competition is null
     */
    protected CompetitionEvent(Competition competition) {
        super("CompetitionService");
        if (competition == null) {
            throw new NullPointerException("Competition cannot be null");
        }
        this.competitionId = competition.getId();
        this.competitionName = competition.getName();
        this.isActive = competition.isActive();
    }
    
    /**
     * Gets the ID of the competition.
     * @return the competition ID
     */
    public Long getCompetitionId() {
        return competitionId;
    }
    
    /**
     * Gets the name of the competition.
     * @return the competition name
     */
    public String getCompetitionName() {
        return competitionName;
    }
    
    /**
     * Gets whether the competition is active.
     * @return true if competition is active
     */
    public boolean isActive() {
        return isActive;
    }
}
