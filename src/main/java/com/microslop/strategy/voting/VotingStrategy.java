package com.microslop.strategy.voting;

import com.microslop.entity.User;
import com.microslop.entity.Competition;

public interface VotingStrategy {
    boolean canVote(User user, Competition competition);
    int calculateVotePoints(User user, Competition competition, int basePoints);
    String getStrategyName();
}