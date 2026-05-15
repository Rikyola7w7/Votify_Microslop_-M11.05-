package com.microslop.service;

import com.microslop.entity.Voter;

/**
 * Service interface for managing Voter operations.
 * Defines business logic for voter registration in competition categories.
 */
public interface VoterService {

    /**
     * Register a user as a voter for a specific competition and category.
     * @param userId the user ID
     * @param competitionId the competition ID
     * @param categoryId the category ID
     * @return the saved Voter entity
     * @throws IllegalStateException if user is already registered for this category
     */
    Voter registerVoter(Long userId, Long competitionId, Long categoryId);

    /**
     * Check if a user is registered as a voter for a specific competition and category.
     * @param userId the user ID
     * @param competitionId the competition ID
     * @param categoryId the category ID
     * @return true if registered, false otherwise
     */
    boolean isRegisteredVoter(Long userId, Long competitionId, Long categoryId);

    /**
     * Check if a user is registered as a voter for any category in a competition.
     * @param userId the user ID
     * @param competitionId the competition ID
     * @return true if registered in any category, false otherwise
     */
    boolean isRegisteredVoterInCompetition(Long userId, Long competitionId);
}
