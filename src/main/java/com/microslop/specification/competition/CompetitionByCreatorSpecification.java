package com.microslop.specification.competition;

import com.microslop.entity.Competition;
import com.microslop.specification.AbstractSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class CompetitionByCreatorSpecification extends AbstractSpecification<Competition> {

    private final String createdBy;

    public CompetitionByCreatorSpecification(String createdBy) {
        if (createdBy == null || createdBy.trim().isEmpty()) {
            throw new IllegalArgumentException("Creator username cannot be null or empty");
        }
        this.createdBy = createdBy.trim();
    }

    @Override
    protected Predicate getPredicates(Root<Competition> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.equal(
                cb.lower(root.get("createdBy")),
                createdBy.toLowerCase()
        );
    }
}
