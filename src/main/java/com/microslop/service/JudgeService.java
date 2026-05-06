package com.microslop.service;

import com.microslop.entity.Judge;
import java.util.List;

/**
 * Service interface for Judge operations.
 * Handles adding and removing judges from competitions.
 */
public interface JudgeService {
    
    /**
     * Get all judges for a competition.
     * @param competitionId the competition ID
     * @return list of judges
     */
    List<Judge> getJudgesByCompetition(Long competitionId);
    
    /**
     * Add a user as a judge to a competition.
     * @param userId the user ID
     * @param competitionId the competition ID
     * @return the created Judge
     */
    Judge addJudge(Long userId, Long competitionId);
    
    /**
     * Remove a user as a judge from a competition.
     * @param userId the user ID
     * @param competitionId the competition ID
     */
    void removeJudge(Long userId, Long competitionId);
    
    /**
     * Check if a user is a judge for a competition.
     * @param userId the user ID
     * @param competitionId the competition ID
     * @return true if the user is a judge
     */
    boolean isJudge(Long userId, Long competitionId);
}
