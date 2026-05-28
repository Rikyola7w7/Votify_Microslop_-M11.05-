package com.microslop.state;

import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;

/**
 * DRAFT state: competition is being configured, not yet active.
 * Can submit projects and edit configuration. Cannot vote.
 */
public class DraftCompetitionState implements CompetitionState {

    @Override
    public String name() {
        return "DRAFT";
    }

    @Override
    public void activate(Competition competition) {
        competition.setStatus(CompetitionStatus.ACTIVE);
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
