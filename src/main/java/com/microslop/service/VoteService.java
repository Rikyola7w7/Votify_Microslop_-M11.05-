package com.microslop.service;

import com.microslop.entity.Vote;

public interface VoteService {

    /**
     * Submit a single vote row for a project.
     * Can be called up to 3 times per user per project.
     */
    Vote submitVote(String userUsername, Long projectId);

    /** Total vote rows a project has received. */
    long countVotesByProject(Long projectId);

    /** Vote rows already cast by this user for this project (0–3). */
    long countVotesByUserAndProject(String userUsername, Long projectId);

    /** Total votes cast by a user across an entire competition. */
    long countVotesPerUserInCompetition(String userId, Long competitionId);
}