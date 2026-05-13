package com.microslop.specification.project;

import com.microslop.entity.Project;
import com.microslop.specification.AbstractSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Specification to filter projects by competition ID.
 *
 * Example:
 *   Specification&lt;Project&gt; competitionProjects =
 *       new ProjectsByCompetitionSpecification(competitionId);
 *   List&lt;Project&gt; results = projectRepository.findAll(competitionProjects);
 *
 * @author Votify Team
 * @version 1.0
 */
public class ProjectsByCompetitionSpecification extends AbstractSpecification<Project> {

    private final Long competitionId;

    /**
     * Creates a new specification for filtering by competition.
     *
     * @param competitionId the ID of the competition
     * @throws IllegalArgumentException if competitionId is null or negative
     */
    public ProjectsByCompetitionSpecification(Long competitionId) {
        if (competitionId == null || competitionId <= 0) {
            throw new IllegalArgumentException("Competition ID must be positive");
        }
        this.competitionId = competitionId;
    }

    @Override
    protected Predicate getPredicates(Root<Project> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.equal(root.get("competition").get("id"), competitionId);
    }
}
