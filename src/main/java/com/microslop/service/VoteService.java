package com.microslop.service;

import com.microslop.entity.Vote;
import com.microslop.observer.subject.VoteEventSubject;
import java.util.List;

/**
 * Service interface for vote management.
 * Extends VoteEventSubject to support observer pattern for vote events.
 * Handles vote submission, counting, and event notification.
 */
public interface VoteService extends VoteEventSubject {

    void submitVote(String userUsername, Long projectId, Long categoryId);

    void submitVote(String userUsername, Long projectId, Long categoryId, int points);

    long countVotesByProject(Long projectId);

    long countVotesByProjectAndCategory(Long projectId, Long categoryId);

    long countVotesByUserAndProject(Long userId, Long projectId);

    long countVotesPerUserInCompetition(Long userId, Long competitionId);

    long countVotesByUserAndCategory(Long userId, Long categoryId);

    long countVotesByUserAndProjectAndCategory(Long userId, Long projectId, Long categoryId);

    long countPointsByUserAndCategory(Long userId, Long categoryId);

    /**
     * Get all votes cast by a user, using Specification pattern.
     * @param userId the user ID
     * @return list of votes by the user
     */
    List<Vote> getVotesByUser(Long userId);

    /**
     * Get all votes for a project, using Specification pattern.
     * @param projectId the project ID
     * @return list of votes for the project
     */
    List<Vote> getVotesByProject(Long projectId);

    /**
     * Get all votes in a category, using Specification pattern.
     * @param categoryId the category ID
     * @return list of votes in the category
     */
    List<Vote> getVotesByCategory(Long categoryId);

    /**
     * Get votes by user and project composed, using Specification pattern.
     * @param userId the user ID
     * @param projectId the project ID
     * @return list of votes matching both criteria
     */
    List<Vote> getVotesByUserAndProject(Long userId, Long projectId);
}