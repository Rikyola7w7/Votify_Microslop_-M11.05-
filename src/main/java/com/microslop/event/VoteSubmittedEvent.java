package com.microslop.event;

import com.microslop.entity.Vote;

/**
 * Event fired when a vote is successfully submitted.
 * Contains all information about the submitted vote.
 * This event is published after a vote has been persisted to the database.
 *
 * @author Votify Team
 * @version 1.0
 */
public class VoteSubmittedEvent extends VoteEvent {
    
    /**
     * Creates a new VoteSubmittedEvent.
     * 
     * @param vote the vote that was submitted
     * @param username the username of the voter
     * @throws NullPointerException if vote is null
     */
    public VoteSubmittedEvent(Vote vote, String username) {
        super(vote, username);
    }
    
    @Override
    public String getEventType() {
        return "VOTE_SUBMITTED";
    }
}
