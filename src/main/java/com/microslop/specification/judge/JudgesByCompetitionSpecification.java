package com.microslop.specification.judge;

import com.microslop.entity.Judge;
import com.microslop.specification.AbstractSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class JudgesByCompetitionSpecification extends AbstractSpecification<Judge> {

    private final Long competitionId;

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
