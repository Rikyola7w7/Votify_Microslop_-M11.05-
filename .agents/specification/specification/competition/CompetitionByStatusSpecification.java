package com.microslop.specification.competition;

import com.microslop.entity.Competition;
import com.microslop.specification.AbstractSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Specification to filter competitions by their active status.
 *
 * Example:
 *   Specification&lt;Competition&gt; activeComps =
 *       new CompetitionByStatusSpecification(true);
 *   List&lt;Competition&gt; results = competitionRepository.findAll(activeComps);
 *
 * @author Votify Team
 * @version 1.0
 */
public class CompetitionByStatusSpecification extends AbstractSpecification<Competition> {

    private final boolean active;

    /**
     * Creates a new specification for filtering by status.
     *
     * @param active true to find active competitions, false for inactive
     */
    public CompetitionByStatusSpecification(boolean active) {
        this.active = active;
    }

    @Override
    protected Predicate getPredicates(Root<Competition> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.equal(root.get("active"), active);
    }
}
