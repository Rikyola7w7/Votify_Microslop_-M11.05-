package com.microslop.state;

import com.microslop.entity.Competition;

public class DraftCompetitionState implements CompetitionState {

    @Override
    public String name() {
        return "DRAFT";
    }

    @Override
    public void activate(Competition competition) {
        competition.setStatus(CompetitionStates.STATUS_ACTIVE);
    }

    @Override
    public boolean canSubmitProjects() {
        return true;
    }

    @Override
    public boolean canEditConfiguration() {
        return true;
    }

    @Override
    public boolean isActiveLegacy() {
        return false;
    }
}
