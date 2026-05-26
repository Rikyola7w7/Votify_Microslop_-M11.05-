package com.microslop.strategy.voting;

import com.microslop.entity.User;
import com.microslop.entity.Competition;
import com.microslop.repository.JudgeRepository;
import org.springframework.stereotype.Component;

@Component
public class JudgesOnlyVotingStrategy implements VotingStrategy {

    private final JudgeRepository judgeRepository;

    public JudgesOnlyVotingStrategy(JudgeRepository judgeRepository) {
        this.judgeRepository = judgeRepository;
    }

    @Override
    public boolean canVote(User user, Competition competition) {
        if (user == null || competition == null) {
            return false;
        }
        if (!competition.canVote()) {
            return false;
        }
        return judgeRepository.existsByUserIdAndCompetitionId(user.getId(), competition.getId());
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
        return "JudgesOnlyVotingStrategy";
    }
}