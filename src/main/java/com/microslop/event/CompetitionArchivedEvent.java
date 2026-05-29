package com.microslop.event;

import com.microslop.entity.Competition;

/**
 * Event fired when a competition is archived.
 */
public class CompetitionArchivedEvent extends CompetitionEvent {

    public CompetitionArchivedEvent(Competition competition) {
        super(competition);
    }

    @Override
    public String getEventType() {
        return "COMPETITION_ARCHIVED";
    }
}
