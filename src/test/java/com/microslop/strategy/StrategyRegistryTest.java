package com.microslop.strategy;

import com.microslop.strategy.voting.VotingStrategy;
import com.microslop.strategy.voting.AllVotingStrategy;
import com.microslop.strategy.voting.JudgesOnlyVotingStrategy;
import com.microslop.strategy.ranking.RankingStrategy;
import com.microslop.strategy.ranking.WeightedScoreRankingStrategy;
import com.microslop.strategy.ranking.AverageScoreRankingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class StrategyRegistryTest {

    private StrategyRegistry registry;

    @BeforeEach
    void setUp() {
        List<VotingStrategy> votingStrategies = List.of(
            new AllVotingStrategy(),
            new JudgesOnlyVotingStrategy(null)
        );
        List<RankingStrategy> rankingStrategies = List.of(
            new WeightedScoreRankingStrategy(null),
            new AverageScoreRankingStrategy(null)
        );
        registry = new StrategyRegistry(votingStrategies, rankingStrategies);
    }

    @Test
    void resolveVotingStrategy_returnsAllVotingStrategyForTypeALL() {
        VotingStrategy strategy = registry.resolveVotingStrategy("ALL");

        assertEquals("AllVotingStrategy", strategy.getStrategyName());
    }

    @Test
    void resolveVotingStrategy_returnsJudgesOnlyForTypeJUDGES() {
        VotingStrategy strategy = registry.resolveVotingStrategy("JUDGES");

        assertEquals("JudgesOnlyVotingStrategy", strategy.getStrategyName());
    }

    @Test
    void resolveVotingStrategy_returnsDefaultForNullType() {
        VotingStrategy strategy = registry.resolveVotingStrategy(null);

        assertNotNull(strategy);
    }

    @Test
    void resolveRankingStrategy_returnsWeightedForTypeWEIGHTED() {
        RankingStrategy strategy = registry.resolveRankingStrategy("WEIGHTED");

        assertEquals("WeightedScoreRankingStrategy", strategy.getStrategyName());
    }

    @Test
    void resolveRankingStrategy_returnsAverageForTypeAVERAGE() {
        RankingStrategy strategy = registry.resolveRankingStrategy("AVERAGE");

        assertEquals("AverageScoreRankingStrategy", strategy.getStrategyName());
    }

    @Test
    void resolveRankingStrategy_returnsDefaultForNullType() {
        RankingStrategy strategy = registry.resolveRankingStrategy(null);

        assertNotNull(strategy);
    }

    @Test
    void getAllVotingStrategies_returnsAllStrategies() {
        List<VotingStrategy> strategies = registry.getAllVotingStrategies();

        assertEquals(2, strategies.size());
    }

    @Test
    void getAllRankingStrategies_returnsAllStrategies() {
        List<RankingStrategy> strategies = registry.getAllRankingStrategies();

        assertEquals(2, strategies.size());
    }
}