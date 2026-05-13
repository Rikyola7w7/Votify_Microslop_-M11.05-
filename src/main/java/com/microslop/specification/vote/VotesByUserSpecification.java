package com.microslop.specification.vote;

import com.microslop.entity.Vote;
import com.microslop.specification.AbstractSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class VotesByUserSpecification extends AbstractSpecification<Vote> {

    private final Long userId;

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
