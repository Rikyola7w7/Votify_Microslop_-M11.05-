package com.microslop.repository;

import com.microslop.entity.Category;
import com.microslop.entity.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;

/**
 * Repository for Category entity.
 * Provides database access methods for Category objects.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    /**
     * Find all categories belonging to a specific competition.
     * @param competition the Competition entity
     * @return list of categories for the competition
     */
    List<Category> findByCompetition(Competition competition);

    /**
     * Find all categories by competition ID.
     * @param competitionId the ID of the competition
     * @return list of categories for the competition
     */
    List<Category> findByCompetitionId(Long competitionId);

    Optional<Category> findByCompetitionIdAndName(Long competitionId, String name);
}
