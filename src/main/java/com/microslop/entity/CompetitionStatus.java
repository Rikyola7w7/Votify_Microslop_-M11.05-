package com.microslop.entity;

import com.microslop.state.*;

/**
 * Enum mapping competition statuses to their state implementations.
 * Each enum constant holds a singleton state object.
 */
public enum CompetitionStatus {
    DRAFT(new DraftCompetitionState()),
    ACTIVE(new ActiveCompetitionState()),
    VOTING_OPEN(new VotingOpenCompetitionState()),
    PAUSED(new PausedCompetitionState()),
    CONCLUDED(new ConcludedCompetitionState()),
    ARCHIVED(new ArchivedCompetitionState());

    private final CompetitionState state;

    CompetitionStatus(CompetitionState state) {
        this.state = state;
    }

    public CompetitionState getState() {
        return state;
    }
}
