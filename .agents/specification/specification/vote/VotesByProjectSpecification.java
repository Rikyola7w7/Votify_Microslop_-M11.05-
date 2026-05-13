package com.microslop.specification.vote;

import com.microslop.entity.Vote;
import com.microslop.specification.AbstractSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Specification to filter votes by project ID.
 *
 * Example:
 *   Specification&lt;Vote&gt; projectVotes =
 *       new VotesByProjectSpecification(projectId);
 *   List&lt;Vote&gt; results = voteRepository.findAll(projectVotes);
 *
 * @author Votify Team
 * @version 1.0
 */
public class VotesByProjectSpecification extends AbstractSpecification<Vote> {

    private final Long projectId;

    /**
     * Creates a new specification for filtering votes by project.
     *
     * @param projectId the ID of the project that received the votes
     * @throws IllegalArgumentException if projectId is null or negative
     */
    public VotesByProjectSpecification(Long projectId) {
        if (projectId == null || projectId <= 0) {
            throw new IllegalArgumentException("Project ID must be positive");
        }
        this.projectId = projectId;
    }

    @Override
    protected Predicate getPredicates(Root<Vote> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.equal(root.get("project").get("id"), projectId);
    }
}
