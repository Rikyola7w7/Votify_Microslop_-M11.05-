package com.microslop.observer.impl;

import com.microslop.event.VoteEvent;
import com.microslop.observer.observer.VoteObserver;
import com.microslop.service.RankingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RankingUpdateObserver implements VoteObserver {

    private static final Logger log = LoggerFactory.getLogger(RankingUpdateObserver.class);
    private final RankingService rankingService;

    public RankingUpdateObserver(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @Override
    public void onVoteSubmitted(VoteEvent event) {
        try {
            Long projectId = event.getProjectId();
            Long competitionId = event.getCompetitionId();
            rankingService.recalculateRankings(competitionId);
            log.debug("Rankings updated for competition {} after vote on project {}",
                     competitionId, projectId);
        } catch (Exception e) {
            log.error("Error updating rankings on vote submission", e);
        }
    }

    @Override
    public void onVoteUndone(VoteEvent event) {
        try {
            Long competitionId = event.getCompetitionId();
            rankingService.recalculateRankings(competitionId);
            log.debug("Rankings recalculated after vote undo in competition {}", competitionId);
        } catch (Exception e) {
            log.error("Error recalculating rankings after vote undo", e);
        }
    }

    @Override
    public void onVoteRedone(VoteEvent event) {
        try {
            Long competitionId = event.getCompetitionId();
            rankingService.recalculateRankings(competitionId);
            log.debug("Rankings recalculated after vote redo in competition {}", competitionId);
        } catch (Exception e) {
            log.error("Error recalculating rankings after vote redo", e);
        }
    }

    @Override
    public String getObserverName() {
        return "RankingUpdateObserver";
    }
}
