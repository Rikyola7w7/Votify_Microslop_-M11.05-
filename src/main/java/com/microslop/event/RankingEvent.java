package com.microslop.event;

/**
 * Abstract base class for ranking-related events.
 * Provides common properties for all ranking events (updated, score changed).
 * All ranking events carry information about project rankings and competition.
 *
 * @author Votify Team
 * @version 1.0
 */
public abstract class RankingEvent extends VotifyEvent {
    
    private final Long competitionId;
    private final Long projectId;
    private final double newScore;
    private final double previousScore;
    
    /**
     * Creates a new RankingEvent.
     * 
     * @param competitionId the competition ID
     * @param projectId the project ID
     * @param newScore the new project score
     * @param previousScore the previous project score
     */
    protected RankingEvent(Long competitionId, Long projectId, double newScore, double previousScore) {
        super("RankingService");
        if (competitionId == null || projectId == null) {
            throw new NullPointerException("Competition ID and Project ID cannot be null");
        }
        this.competitionId = competitionId;
        this.projectId = projectId;
        this.newScore = newScore;
        this.previousScore = previousScore;
    }
    
    /**
     * Gets the ID of the competition.
     * @return the competition ID
     */
    public Long getCompetitionId() {
        return competitionId;
    }
    
    /**
     * Gets the ID of the project.
     * @return the project ID
     */
    public Long getProjectId() {
        return projectId;
    }
    
    /**
     * Gets the new score of the project.
     * @return the new score
     */
    public double getNewScore() {
        return newScore;
    }
    
    /**
     * Gets the previous score of the project.
     * @return the previous score
     */
    public double getPreviousScore() {
        return previousScore;
    }
}
