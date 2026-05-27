package com.microslop.state;

import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;

/**
 * PAUSED state: voting is paused (temporarily stopped).
 * Cannot submit projects, vote, or edit configuration.
 * Can resume voting or conclude.
 */
public class PausedCompetitionState implements CompetitionState {

    @Override
    public void openVoting(Competition competition) {
        competition.setStatus(CompetitionStatus.VOTING_OPEN);
    }

    @Override
    public void conclude(Competition competition) {
        competition.setStatus(CompetitionStatus.CONCLUDED);
    }

    @Override
    public boolean isActiveLegacy() {
        return true;
    }
}
