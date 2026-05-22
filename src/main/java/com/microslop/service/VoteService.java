package com.microslop.service;

import com.microslop.entity.Vote;
import com.microslop.observer.subject.VoteEventSubject;
import java.util.List;
import java.util.Map;

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

    Map<Long, Long> countVotesByProjectIds(List<Long> projectIds);

    Map<Long, Long> countVotesByProjectIdsAndCategory(List<Long> projectIds, Long categoryId);

    Map<Long, Long> countUserVotesByProjectIdsAndCategory(List<Long> projectIds, Long userId, Long categoryId);

    List<Vote> getVotesByUser(Long userId);

    List<Vote> getVotesByProject(Long projectId);

    List<Vote> getVotesByCategory(Long categoryId);

    List<Vote> getVotesByUserAndProject(Long userId, Long projectId);

    com.microslop.strategy.StrategyRegistry getStrategyRegistry();
}