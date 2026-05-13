package com.microslop.specification.project;

import com.microslop.entity.Project;
import com.microslop.specification.AbstractSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class ProjectsByCreatorSpecification extends AbstractSpecification<Project> {

    private final Long userId;

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
