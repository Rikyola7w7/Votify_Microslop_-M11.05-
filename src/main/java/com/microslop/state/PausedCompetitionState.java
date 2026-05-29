package com.microslop.state;

import com.microslop.entity.Competition;

/**
 * PAUSED state: voting is paused (temporarily stopped).
 * Cannot submit projects, vote, or edit configuration.
 * Can resume voting or conclude.
 */
public class PausedCompetitionState implements CompetitionState {

    @Override
    public String name() {
        return "PAUSED";
    }

    @Override
    public void openVoting(Competition competition) {
        competition.setStatus(CompetitionStates.STATUS_VOTING_OPEN);
    }

    @Override
    public void conclude(Competition competition) {
        competition.setStatus(CompetitionStates.STATUS_CONCLUDED);
    }

    @Override
    public boolean isActiveLegacy() {
        return true;
    }
}
