package com.microslop.event;

import com.microslop.entity.Competition;

/**
 * Event fired when voting is paused for a competition.
 */
public class CompetitionVotingPausedEvent extends CompetitionEvent {

    public CompetitionVotingPausedEvent(Competition competition) {
        super(competition);
    }

    @Override
    public String getEventType() {
        return "COMPETITION_VOTING_PAUSED";
    }
}
