package com.microslop.specification;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Abstract base class for all Specifications in Votify.
 * Provides common functionality and composition methods.
 *
 * Subclasses must implement {@link #getPredicates(Root, CriteriaQuery, CriteriaBuilder)}
 * to define their filtering logic.
 *
 * @param <T> the entity type this specification applies to
 *
 * @author Votify Team
 * @version 1.0
 */
public abstract class AbstractSpecification<T> implements Specification<T> {

    /**
     * Subclasses must implement this method to define the predicate logic.
     * This method is called by toPredicate() to build the actual query criteria.
     *
     * @param root the root entity
     * @param query the criteria query
     * @param cb the criteria builder
     * @return the predicate representing this specification's criteria
     */
    protected abstract Predicate getPredicates(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb);

    /**
     * Converts this specification to a JPA Predicate.
     * Delegates to getPredicates() for subclass-specific logic.
     *
     * @param root the root entity
     * @param query the criteria query
     * @param cb the criteria builder
     * @return the predicate
     */
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return getPredicates(root, query, cb);
    }

    /**
     * Combines this specification with another using AND logic.
     * Both specifications must be true for the result to be true.
     *
     * @param other the other specification to combine with
     * @return a new specification representing the AND combination
     */
    @Override
    public Specification<T> and(Specification<T> other) {
        return (root, query, cb) -> cb.and(
                this.toPredicate(root, query, cb),
                other.toPredicate(root, query, cb)
        );
    }

    /**
     * Combines this specification with another using OR logic.
     * Either specification can be true for the result to be true.
     *
     * @param other the other specification to combine with
     * @return a new specification representing the OR combination
     */
    @Override
    public Specification<T> or(Specification<T> other) {
        return (root, query, cb) -> cb.or(
                this.toPredicate(root, query, cb),
                other.toPredicate(root, query, cb)
        );
    }

    /**
     * Negates this specification using NOT logic.
     * The result is true when this specification is false.
     *
     * @return a new specification representing the negation
     */
    public Specification<T> not() {
        return (root, query, cb) -> cb.not(
                this.toPredicate(root, query, cb)
        );
    }
}
