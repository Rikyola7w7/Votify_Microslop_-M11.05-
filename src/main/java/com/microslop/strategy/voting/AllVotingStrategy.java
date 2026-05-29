package com.microslop.strategy.voting;

import com.microslop.entity.User;
import com.microslop.entity.Competition;
import org.springframework.stereotype.Component;

@Component
public class AllVotingStrategy implements VotingStrategy {

    @Override
    public boolean canVote(User user, Competition competition) {
        if (user == null || competition == null) {
            return false;
        }
        return competition.canVote();
    }

    @Override
    public int calculateVotePoints(User user, Competition competition, int basePoints) {
        if (basePoints <= 0) {
            return 0;
        }
        return basePoints;
    }

    @Override
    public String getStrategyName() {
        return "AllVotingStrategy";
    }
}