package com.microslop.state;

import com.microslop.entity.Competition;

/**
 * CONCLUDED state: competition has ended.
 * Cannot vote, submit projects, or edit. Can archive or reopen.
 */
public class ConcludedCompetitionState implements CompetitionState {

    @Override
    public String name() {
        return "CONCLUDED";
    }

    @Override
    public void archive(Competition competition) {
        competition.setStatus(CompetitionStates.STATUS_ARCHIVED);
    }

    @Override
    public void reopen(Competition competition) {
        competition.setStatus(CompetitionStates.STATUS_ACTIVE);
    }

    @Override
    public boolean isActiveLegacy() {
        return false;
    }
}
