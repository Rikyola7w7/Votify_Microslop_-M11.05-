package com.microslop.state;

import com.microslop.entity.Competition;
public class ActiveCompetitionState implements CompetitionState {

    @Override
    public String name() {
        return "ACTIVE";
    }

    @Override
    public void deactivate(Competition competition) {
        competition.setStatus(CompetitionStates.STATUS_DRAFT);
    }

    @Override
    public void openVoting(Competition competition) {
        competition.setStatus(CompetitionStates.STATUS_VOTING_OPEN);
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
    public boolean canSubmitProjects() {
        return true;
    }

    @Override
    public boolean canVote() {
        return true;
    }

    @Override
    public boolean canEditConfiguration() {
        return true;
    }

    @Override
    public boolean isActiveLegacy() {
        return true;
    }
}
