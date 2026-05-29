package com.microslop.event;

import com.microslop.entity.Vote;

/**
 * Event fired when a vote is undone.
 * Published when a vote is removed or reverted via the undo command.
 * This event carries the same information as a vote submission but represents removal.
 *
 * @author Votify Team
 * @version 1.0
 */
public class VoteUndoneEvent extends VoteEvent {
    
    /**
     * Creates a new VoteUndoneEvent.
     * 
     * @param vote the vote that was undone
     * @param username the username of the voter
     * @throws NullPointerException if vote is null
     */
    public VoteUndoneEvent(Vote vote, String username) {
        super(vote, username);
    }
    
    @Override
    public String getEventType() {
        return "VOTE_UNDONE";
    }
}
