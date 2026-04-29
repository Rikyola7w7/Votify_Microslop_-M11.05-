package com.microslop.repository;

import com.microslop.entity.Judge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Repository for Judge entity.
 * Provides data access operations for judges in competitions.
 */
@Repository
public interface JudgeRepository extends JpaRepository<Judge, Long> {
    
    /**
     * Find all judges for a specific competition.
     * @param competitionId the competition ID
     * @return list of judges for the competition
     */
    List<Judge> findByCompetitionId(Long competitionId);
    
    /**
     * Check if a user is a judge for a specific competition.
     * @param userId the user ID
     * @param competitionId the competition ID
     * @return true if the user is a judge, false otherwise
     */
    @Query("SELECT COUNT(j) > 0 FROM Judge j WHERE j.user.id = :userId AND j.competition.id = :competitionId")
    boolean existsByUserIdAndCompetitionId(@Param("userId") Long userId, @Param("competitionId") Long competitionId);
    
    /**
     * Delete a judge by user ID and competition ID.
     * @param userId the user ID
     * @param competitionId the competition ID
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM Judge j WHERE j.user.id = :userId AND j.competition.id = :competitionId")
    void deleteByUserIdAndCompetitionId(@Param("userId") Long userId, @Param("competitionId") Long competitionId);
}
