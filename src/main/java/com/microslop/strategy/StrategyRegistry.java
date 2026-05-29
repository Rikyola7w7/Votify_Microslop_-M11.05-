package com.microslop.strategy;

import com.microslop.strategy.voting.VotingStrategy;
import com.microslop.strategy.ranking.RankingStrategy;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Component
public class StrategyRegistry {

    private static final String DEFAULT_VOTING_FALLBACK = "AllVotingStrategy";
    private static final String DEFAULT_RANKING_FALLBACK = "AverageScoreRankingStrategy";

    private final Map<String, VotingStrategy> votingStrategies;
    private final Map<String, RankingStrategy> rankingStrategies;

    public StrategyRegistry(List<VotingStrategy> votingStrategyList,
                           List<RankingStrategy> rankingStrategyList) {
        this.votingStrategies = new HashMap<>();
        for (VotingStrategy strategy : votingStrategyList) {
            this.votingStrategies.put(strategy.getStrategyName(), strategy);
        }
        this.rankingStrategies = new HashMap<>();
        for (RankingStrategy strategy : rankingStrategyList) {
            this.rankingStrategies.put(strategy.getStrategyName(), strategy);
        }
    }

    public VotingStrategy resolveVotingStrategy(String type) {
        if (type != null && !type.isBlank()) {
            for (VotingStrategy strategy : votingStrategies.values()) {
                if (strategy.getStrategyName().equalsIgnoreCase(type.trim())) {
                    return strategy;
                }
            }
        }
        return votingStrategies.getOrDefault(DEFAULT_VOTING_FALLBACK,
            new com.microslop.strategy.voting.AllVotingStrategy());
    }

    public RankingStrategy resolveRankingStrategy(String type) {
        if (type != null && !type.isBlank()) {
            for (RankingStrategy strategy : rankingStrategies.values()) {
                if (strategy.getStrategyName().equalsIgnoreCase(type.trim())) {
                    return strategy;
                }
            }
        }
        return rankingStrategies.getOrDefault(DEFAULT_RANKING_FALLBACK,
            new com.microslop.strategy.ranking.AverageScoreRankingStrategy());
    }

    public VotingStrategy getVotingStrategyByName(String name) {
        return votingStrategies.get(name);
    }

    public RankingStrategy getRankingStrategyByName(String name) {
        return rankingStrategies.get(name);
    }

    public List<VotingStrategy> getAllVotingStrategies() {
        return List.copyOf(votingStrategies.values());
    }

    public List<RankingStrategy> getAllRankingStrategies() {
        return List.copyOf(rankingStrategies.values());
    }
}