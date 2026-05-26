package com.microslop.state;

import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;

public class ActiveCompetitionState implements CompetitionState {

    @Override
    public void deactivate(Competition competition) {
        competition.setStatus(CompetitionStatus.DRAFT);
    }

    @Override
    public void openVoting(Competition competition) {
        competition.setStatus(CompetitionStatus.VOTING_OPEN);
    }

    @Override
    public void pauseVoting(Competition competition) {
        competition.setStatus(CompetitionStatus.PAUSED);
    }

    @Override
    public void conclude(Competition competition) {
        competition.setStatus(CompetitionStatus.CONCLUDED);
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
