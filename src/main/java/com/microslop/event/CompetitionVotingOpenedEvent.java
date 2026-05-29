package com.microslop.event;

import com.microslop.entity.Competition;

/**
 * Event fired when voting is opened for a competition.
 */
public class CompetitionVotingOpenedEvent extends CompetitionEvent {

    public CompetitionVotingOpenedEvent(Competition competition) {
        super(competition);
    }

    @Override
    public String getEventType() {
        return "COMPETITION_VOTING_OPENED";
    }
}
