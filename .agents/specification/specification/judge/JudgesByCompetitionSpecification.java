package com.microslop.specification.judge;

import com.microslop.entity.Judge;
import com.microslop.specification.AbstractSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Specification to filter judges by competition ID.
 *
 * Example:
 *   Specification&lt;Judge&gt; competitionJudges =
 *       new JudgesByCompetitionSpecification(competitionId);
 *   List&lt;Judge&gt; results = judgeRepository.findAll(competitionJudges);
 *
 * @author Votify Team
 * @version 1.0
 */
public class JudgesByCompetitionSpecification extends AbstractSpecification<Judge> {

    private final Long competitionId;

    /**
     * Creates a new specification for filtering judges by competition.
     *
     * @param competitionId the ID of the competition
     * @throws IllegalArgumentException if competitionId is null or negative
     */
    public JudgesByCompetitionSpecification(Long competitionId) {
        if (competitionId == null || competitionId <= 0) {
            throw new IllegalArgumentException("Competition ID must be positive");
        }
        this.competitionId = competitionId;
    }

    @Override
    protected Predicate getPredicates(Root<Judge> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.equal(root.get("competition").get("id"), competitionId);
    }
}
