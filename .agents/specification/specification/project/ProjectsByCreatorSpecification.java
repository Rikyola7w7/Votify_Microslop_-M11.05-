package com.microslop.specification.project;

import com.microslop.entity.Project;
import com.microslop.specification.AbstractSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Specification to filter projects by creator user ID.
 *
 * Example:
 *   Specification&lt;Project&gt; userProjects =
 *       new ProjectsByCreatorSpecification(userId);
 *   List&lt;Project&gt; results = projectRepository.findAll(userProjects);
 *
 * @author Votify Team
 * @version 1.0
 */
public class ProjectsByCreatorSpecification extends AbstractSpecification<Project> {

    private final Long userId;

    /**
     * Creates a new specification for filtering by creator user ID.
     *
     * @param userId the ID of the creator user
     * @throws IllegalArgumentException if userId is null or negative
     */
    public ProjectsByCreatorSpecification(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        this.userId = userId;
    }

    @Override
    protected Predicate getPredicates(Root<Project> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.equal(root.get("createdByUser").get("id"), userId);
    }
}
