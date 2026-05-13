package com.microslop.specification.competition;

import com.microslop.entity.Competition;
import com.microslop.specification.AbstractSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Specification to filter competitions by name (case-insensitive substring match).
 *
 * Example:
 *   Specification&lt;Competition&gt; searchComps =
 *       new CompetitionByNameSpecification("TechConf");
 *   List&lt;Competition&gt; results = competitionRepository.findAll(searchComps);
 *
 * @author Votify Team
 * @version 1.0
 */
public class CompetitionByNameSpecification extends AbstractSpecification<Competition> {

    private final String name;

    /**
     * Creates a new specification for filtering by name.
     *
     * @param name the name to search for (case-insensitive, partial match)
     * @throws IllegalArgumentException if name is null or empty
     */
    public CompetitionByNameSpecification(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Competition name cannot be null or empty");
        }
        this.name = name.trim();
    }

    @Override
    protected Predicate getPredicates(Root<Competition> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.like(
                cb.lower(root.get("name")),
                "%" + name.toLowerCase() + "%"
        );
    }
}
