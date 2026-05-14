package com.microslop.specification.competition;

import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;
import com.microslop.specification.AbstractSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Specification that filters competitions by exact CompetitionStatus enum value.
 */
public class CompetitionByStatusEnumSpecification extends AbstractSpecification<Competition> {

    private final CompetitionStatus status;

    public CompetitionByStatusEnumSpecification(CompetitionStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("CompetitionStatus cannot be null");
        }
        this.status = status;
    }

    @Override
    protected Predicate getPredicates(Root<Competition> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.equal(root.get("status"), status);
    }
}
