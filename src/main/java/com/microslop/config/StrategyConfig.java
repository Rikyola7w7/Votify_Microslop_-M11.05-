package com.microslop.config;

import com.microslop.strategy.StrategyRegistry;
import com.microslop.strategy.voting.VotingStrategy;
import com.microslop.strategy.ranking.RankingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class StrategyConfig {

    @Bean
    public StrategyRegistry strategyRegistry(List<VotingStrategy> votingStrategies,
                                             List<RankingStrategy> rankingStrategies) {
        return new StrategyRegistry(votingStrategies, rankingStrategies);
    }
}