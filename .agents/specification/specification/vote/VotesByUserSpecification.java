package com.microslop.specification.vote;

import com.microslop.entity.Vote;
import com.microslop.specification.AbstractSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Specification to filter votes by user ID.
 *
 * Example:
 *   Specification&lt;Vote&gt; userVotes =
 *       new VotesByUserSpecification(userId);
 *   List&lt;Vote&gt; results = voteRepository.findAll(userVotes);
 *
 * @author Votify Team
 * @version 1.0
 */
public class VotesByUserSpecification extends AbstractSpecification<Vote> {

    private final Long userId;

    /**
     * Creates a new specification for filtering votes by user.
     *
     * @param userId the ID of the user who cast the votes
     * @throws IllegalArgumentException if userId is null or negative
     */
    public VotesByUserSpecification(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        this.userId = userId;
    }

    @Override
    protected Predicate getPredicates(Root<Vote> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.equal(root.get("user").get("id"), userId);
    }
}
