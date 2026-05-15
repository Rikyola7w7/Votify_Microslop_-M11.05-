package com.microslop.specification.project;

import com.microslop.entity.Project;
import com.microslop.specification.AbstractSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class ProjectsByCompetitionSpecification extends AbstractSpecification<Project> {

    private final Long competitionId;

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
