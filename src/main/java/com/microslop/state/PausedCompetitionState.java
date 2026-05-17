package com.microslop.state;

import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;

public class PausedCompetitionState implements CompetitionState {

    @Override
    public void openVoting(Competition competition) {
        competition.setStatus(CompetitionStatus.ACTIVE);
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