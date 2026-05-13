package com.microslop.service;

import com.microslop.observer.subject.VoteEventSubject;

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
}