package com.microslop.state;

import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;

/**
 * VOTING_OPEN state: voting is active and accepting votes.
 * Cannot submit projects or edit configuration. Can vote.
 */
public class VotingOpenCompetitionState implements CompetitionState {

    @Override
    public void pauseVoting(Competition competition) {
        competition.setStatus(CompetitionStatus.ACTIVE);
    }

    @Override
    public void conclude(Competition competition) {
        competition.setStatus(CompetitionStatus.CONCLUDED);
    }

    @Override
    public boolean canVote() {
        return true;
    }

    @Override
    public boolean isActiveLegacy() {
        return true;
    }
}
