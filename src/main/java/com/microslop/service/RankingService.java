package com.microslop.service;

/**
 * Service for calculating and managing project rankings.
 * Handles ranking calculations with judge multipliers and vote aggregation.
 * Responsible for maintaining accurate project rankings within competitions.
 *
 * @author Votify Team
 * @version 1.0
 */
public interface RankingService {
    
    /**
     * Recalculate rankings for a competition.
     * Should be called when votes are submitted or removed.
     * Recalculates rankings considering judge multipliers and voting rules.
     * 
     * @param competitionId the competition ID
     */
    void recalculateRankings(Long competitionId);
    
    /**
     * Calculate score for a project with multipliers.
     * Computes the total score of a project considering vote weights and judge multipliers.
     * 
     * @param projectId the project ID
     * @param competitionId the competition ID
     * @return calculated score
     */
    double calculateProjectScore(Long projectId, Long competitionId);
}
