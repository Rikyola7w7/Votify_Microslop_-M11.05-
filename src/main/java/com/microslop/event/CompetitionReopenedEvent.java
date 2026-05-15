package com.microslop.event;

import com.microslop.entity.Competition;

/**
 * Event fired when a concluded competition is reopened.
 */
public class CompetitionReopenedEvent extends CompetitionEvent {

    public CompetitionReopenedEvent(Competition competition) {
        super(competition);
    }

    @Override
    public String getEventType() {
        return "COMPETITION_REOPENED";
    }
}
