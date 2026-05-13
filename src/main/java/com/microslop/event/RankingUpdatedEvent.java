package com.microslop.event;

/**
 * Event fired when project rankings are updated.
 * Published when rankings for all projects in a competition need to be recalculated.
 *
 * @author Votify Team
 * @version 1.0
 */
public class RankingUpdatedEvent extends RankingEvent {
    
    /**
     * Creates a new RankingUpdatedEvent.
     * 
     * @param competitionId the competition ID whose rankings were updated
     * @param projectId the project ID with updated ranking
     * @param newScore the new score
     * @param previousScore the previous score
     */
    public RankingUpdatedEvent(Long competitionId, Long projectId, double newScore, double previousScore) {
        super(competitionId, projectId, newScore, previousScore);
    }
    
    @Override
    public String getEventType() {
        return "RANKING_UPDATED";
    }
}
