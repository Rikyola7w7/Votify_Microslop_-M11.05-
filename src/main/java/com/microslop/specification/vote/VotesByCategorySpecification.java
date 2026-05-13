package com.microslop.specification.vote;

import com.microslop.entity.Vote;
import com.microslop.specification.AbstractSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class VotesByCategorySpecification extends AbstractSpecification<Vote> {

    private final Long categoryId;

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
