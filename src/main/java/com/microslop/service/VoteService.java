package com.microslop.service;


public interface VoteService {

    void submitVote(String userUsername, Long projectId, Long categoryId);

    long countVotesByProject(Long projectId);

    long countVotesByUserAndProject(Long userId, Long projectId);

    long countVotesPerUserInCompetition(Long userId, Long competitionId);
}