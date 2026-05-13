package com.microslop.specification.competition;

import com.microslop.entity.Competition;
import com.microslop.specification.AbstractSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Specification to filter competitions by creator username.
 *
 * Example:
 *   Specification&lt;Competition&gt; userComps =
 *       new CompetitionByCreatorSpecification("john_doe");
 *   List&lt;Competition&gt; results = competitionRepository.findAll(userComps);
 *
 * @author Votify Team
 * @version 1.0
 */
public class CompetitionByCreatorSpecification extends AbstractSpecification<Competition> {

    private final String createdBy;

    /**
     * Creates a new specification for filtering by creator.
     *
     * @param createdBy the username of the creator (case-insensitive)
     * @throws IllegalArgumentException if createdBy is null or empty
     */
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
