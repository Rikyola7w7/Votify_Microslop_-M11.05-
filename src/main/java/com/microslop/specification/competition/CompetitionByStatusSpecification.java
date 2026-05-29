package com.microslop.specification.competition;

import com.microslop.entity.Competition;
import com.microslop.state.CompetitionStates;
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
            return root.get("status").in(List.of(
                CompetitionStates.STATUS_ACTIVE, CompetitionStates.STATUS_PAUSED));
        } else {
            return root.get("status").in(List.of(
                CompetitionStates.STATUS_DRAFT, CompetitionStates.STATUS_CONCLUDED,
                CompetitionStates.STATUS_ARCHIVED));
        }
    }
}
