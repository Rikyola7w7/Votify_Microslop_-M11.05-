package com.microslop.event;

import com.microslop.entity.Vote;

/**
 * Event fired when a vote is redone.
 * Published when a previously undone vote is restored via the redo command.
 * This event carries the same information as a vote submission.
 *
 * @author Votify Team
 * @version 1.0
 */
public class VoteRedoneEvent extends VoteEvent {
    
    /**
     * Creates a new VoteRedoneEvent.
     * 
     * @param vote the vote that was redone
     * @param username the username of the voter
     * @throws NullPointerException if vote is null
     */
    public VoteRedoneEvent(Vote vote, String username) {
        super(vote, username);
    }
    
    @Override
    public String getEventType() {
        return "VOTE_REDONE";
    }
}
