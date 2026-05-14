package com.microslop.strategy;

public enum StrategyType {
    VOTING_ALL("ALL", "AllVotingStrategy"),
    VOTING_JUDGES("JUDGES", "JudgesOnlyVotingStrategy"),
    RANKING_WEIGHTED("WEIGHTED", "WeightedScoreRankingStrategy"),
    RANKING_AVERAGE("AVERAGE", "AverageScoreRankingStrategy"),
    RANKING_NORMALIZED("NORMALIZED", "NormalizedScoreRankingStrategy");

    private final String type;
    private final String beanName;

    StrategyType(String type, String beanName) {
        this.type = type;
        this.beanName = beanName;
    }

    public String getType() {
        return type;
    }

    public String getBeanName() {
        return beanName;
    }

    public static StrategyType fromVotingType(String votingType) {
        if (votingType == null) {
            return VOTING_ALL;
        }
        for (StrategyType st : values()) {
            if (st.type.equals(votingType) && st.name().startsWith("VOTING_")) {
                return st;
            }
        }
        return VOTING_ALL;
    }

    public static StrategyType fromRankingType(String rankingType) {
        if (rankingType == null) {
            return RANKING_WEIGHTED;
        }
        for (StrategyType st : values()) {
            if (st.type.equals(rankingType) && st.name().startsWith("RANKING_")) {
                return st;
            }
        }
        return RANKING_WEIGHTED;
    }
}