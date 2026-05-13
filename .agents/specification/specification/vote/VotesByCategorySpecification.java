package com.microslop.specification.vote;

import com.microslop.entity.Vote;
import com.microslop.specification.AbstractSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Specification to filter votes by category ID.
 *
 * Example:
 *   Specification&lt;Vote&gt; categoryVotes =
 *       new VotesByCategorySpecification(categoryId);
 *   List&lt;Vote&gt; results = voteRepository.findAll(categoryVotes);
 *
 * @author Votify Team
 * @version 1.0
 */
public class VotesByCategorySpecification extends AbstractSpecification<Vote> {

    private final Long categoryId;

    /**
     * Creates a new specification for filtering votes by category.
     *
     * @param categoryId the ID of the voting category
     * @throws IllegalArgumentException if categoryId is null or negative
     */
    public VotesByCategorySpecification(Long categoryId) {
        if (categoryId == null || categoryId <= 0) {
            throw new IllegalArgumentException("Category ID must be positive");
        }
        this.categoryId = categoryId;
    }

    @Override
    protected Predicate getPredicates(Root<Vote> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.equal(root.get("category").get("id"), categoryId);
    }
}
