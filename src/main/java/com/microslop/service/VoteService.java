package com.microslop.service;

import com.microslop.entity.Vote;

public interface VoteService {

    void submitVote(String userUsername, Long projectId);

    long countVotesByProject(Long projectId);

    long countVotesByUserAndProject(String userUsername, Long projectId);

    long countVotesPerUserInCompetition(String userId, Long competitionId);
}