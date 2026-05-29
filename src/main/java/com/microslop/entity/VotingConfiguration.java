package com.microslop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VotingConfiguration {

    @Column(name = "vote_type", length = 20)
    private String voteType = "NORMAL";

    @Column(name = "voting_strategy_type", length = 50)
    private String votingStrategyType = "ALL";

    @Column(name = "ranking_strategy_type", length = 50)
    private String rankingStrategyType = "WEIGHTED";

    @Column(name = "max_votes_per_person", columnDefinition = "integer default 1")
    private Integer maxVotesPerPerson = 1;

    @Column(name = "scale_min")
    private Integer scaleMin = 0;

    @Column(name = "scale_max")
    private Integer scaleMax = 10;
}
