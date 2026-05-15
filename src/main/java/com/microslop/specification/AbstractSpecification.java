package com.microslop.specification;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public abstract class AbstractSpecification<T> implements Specification<T> {

    protected abstract Predicate getPredicates(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb);

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return getPredicates(root, query, cb);
    }

    @Override
    public Specification<T> and(Specification<T> other) {
        return (root, query, cb) -> cb.and(
                this.toPredicate(root, query, cb),
                other.toPredicate(root, query, cb)
        );
    }

    @Override
    public Specification<T> or(Specification<T> other) {
        return (root, query, cb) -> cb.or(
                this.toPredicate(root, query, cb),
                other.toPredicate(root, query, cb)
        );
    }

    public Specification<T> not() {
        return (root, query, cb) -> cb.not(
                this.toPredicate(root, query, cb)
        );
    }
}
