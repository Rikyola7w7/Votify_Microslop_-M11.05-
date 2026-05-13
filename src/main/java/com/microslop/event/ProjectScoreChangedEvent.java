package com.microslop.event;

/**
 * Event fired when a project's score changes.
 * Published when a project's score is updated due to a vote or other scoring change.
 *
 * @author Votify Team
 * @version 1.0
 */
public class ProjectScoreChangedEvent extends RankingEvent {
    
    /**
     * Creates a new ProjectScoreChangedEvent.
     * 
     * @param competitionId the competition ID
     * @param projectId the project ID whose score changed
     * @param newScore the new score
     * @param previousScore the previous score
     */
    public ProjectScoreChangedEvent(Long competitionId, Long projectId, double newScore, double previousScore) {
        super(competitionId, projectId, newScore, previousScore);
    }
    
    @Override
    public String getEventType() {
        return "PROJECT_SCORE_CHANGED";
    }
}
