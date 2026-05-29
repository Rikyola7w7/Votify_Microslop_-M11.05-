package com.microslop.event;

import com.microslop.entity.Vote;

/**
 * Abstract base class for vote-related events.
 * Provides common properties for all vote events (submitted, undone, redone).
 * All vote events carry information about the vote, voter, and affected project/competition.
 *
 * @author Votify Team
 * @version 1.0
 */
public abstract class VoteEvent extends VotifyEvent {
    
    private final Long voteId;
    private final Long userId;
    private final String username;
    private final Long projectId;
    private final Long categoryId;
    private final Long competitionId;
    private final Integer points;
    
    /**
     * Creates a new VoteEvent from a Vote entity.
     * Extracts all necessary information from the vote and related entities.
     * 
     * @param vote the vote entity
     * @param username the username of the voter
     * @throws NullPointerException if vote is null
     */
    protected VoteEvent(Vote vote, String username) {
        super("VoteService");
        if (vote == null) {
            throw new NullPointerException("Vote cannot be null");
        }
        this.voteId = vote.getId();
        this.userId = vote.getUser().getId();
        this.username = username;
        this.projectId = vote.getProject().getId();
        this.categoryId = vote.getCategory().getId();
        this.competitionId = vote.getProject().getCompetition().getId();
        this.points = vote.getPoints();
    }
    
    /**
     * Gets the ID of the vote.
     * @return the vote ID
     */
    public Long getVoteId() {
        return voteId;
    }
    
    /**
     * Gets the ID of the user who voted.
     * @return the user ID
     */
    public Long getUserId() {
        return userId;
    }
    
    /**
     * Gets the username of the voter.
     * @return the username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Gets the ID of the project that was voted on.
     * @return the project ID
     */
    public Long getProjectId() {
        return projectId;
    }
    
    /**
     * Gets the ID of the category for this vote.
     * @return the category ID
     */
    public Long getCategoryId() {
        return categoryId;
    }
    
    /**
     * Gets the ID of the competition containing the project.
     * @return the competition ID
     */
    public Long getCompetitionId() {
        return competitionId;
    }
    
    /**
     * Gets the points awarded by this vote.
     * @return the vote points
     */
    public Integer getPoints() {
        return points;
    }
}
