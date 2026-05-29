package com.microslop.state;

import com.microslop.entity.Competition;

/**
 * VOTING_OPEN state: voting is active and accepting votes.
 * Cannot submit projects or edit configuration. Can vote.
 */
public class VotingOpenCompetitionState implements CompetitionState {

    @Override
    public String name() {
        return "VOTING_OPEN";
    }

    @Override
    public void pauseVoting(Competition competition) {
        competition.setStatus(CompetitionStates.STATUS_PAUSED);
    }

    @Override
    public void conclude(Competition competition) {
        competition.setStatus(CompetitionStates.STATUS_CONCLUDED);
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
