package com.microslop.repository;

import com.microslop.entity.Judge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
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
    boolean existsByUserIdAndCompetitionId(Long userId, Long competitionId);
    
    /**
     * Delete a judge by user ID and competition ID.
     * @param userId the user ID
     * @param competitionId the competition ID
     */
    void deleteByUserIdAndCompetitionId(Long userId, Long competitionId);
}
