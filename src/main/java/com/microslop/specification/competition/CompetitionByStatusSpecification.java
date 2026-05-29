package com.microslop.specification.competition;

import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;
import com.microslop.specification.AbstractSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;

public class CompetitionByStatusSpecification extends AbstractSpecification<Competition> {

    private final boolean active;

    public CompetitionByStatusSpecification(boolean active) {
        this.active = active;
    }

    @Override
    protected Predicate getPredicates(Root<Competition> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (active) {
            return root.get("status").in(List.of(CompetitionStatus.ACTIVE, CompetitionStatus.PAUSED));
        } else {
            return root.get("status").in(List.of(CompetitionStatus.DRAFT, CompetitionStatus.CONCLUDED, CompetitionStatus.ARCHIVED));
        }
    }
}
