package com.microslop.repository;

import com.microslop.entity.Competition;
import com.microslop.entity.Voter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Voter entity.
 * Provides data access operations for voter registrations in competitions.
 */
@Repository
public interface VoterRepository extends JpaRepository<Voter, Long>, JpaSpecificationExecutor<Voter> {

    /**
     * Check if a user is registered as a voter for a specific competition and category.
     * @param userId the user ID
     * @param competitionId the competition ID
     * @param categoryId the category ID
     * @return true if the user is registered, false otherwise
     */
    @Query("SELECT COUNT(v) > 0 FROM Voter v WHERE v.user.id = :userId AND v.competition.id = :competitionId AND v.category.id = :categoryId")
    boolean existsByUserIdAndCompetitionIdAndCategoryId(@Param("userId") Long userId,
                                                         @Param("competitionId") Long competitionId,
                                                         @Param("categoryId") Long categoryId);

    /**
     * Check if a user is registered as a voter for any category in a competition.
     * @param userId the user ID
     * @param competitionId the competition ID
     * @return true if the user is registered in any category, false otherwise
     */
    @Query("SELECT COUNT(v) > 0 FROM Voter v WHERE v.user.id = :userId AND v.competition.id = :competitionId")
    boolean existsByUserIdAndCompetitionId(@Param("userId") Long userId,
                                            @Param("competitionId") Long competitionId);

    /**
     * Find all voters registered for a specific competition (distinct users)
     * @param competition the competition
     * @return list of distinct voters
     */
    @Query("SELECT DISTINCT v FROM Voter v WHERE v.competition = :competition")
    List<Voter> findByCompetition(@Param("competition") Competition competition);

    java.util.Optional<Voter> findByUserIdAndCompetitionIdAndCategoryId(
            @Param("userId") Long userId,
            @Param("competitionId") Long competitionId,
            @Param("categoryId") Long categoryId);
}
