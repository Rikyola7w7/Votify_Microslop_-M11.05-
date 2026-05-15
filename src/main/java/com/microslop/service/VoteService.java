package com.microslop.service;

import com.microslop.entity.Vote;
import com.microslop.observer.subject.VoteEventSubject;
import java.util.List;

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

void submitScaleVote(String userUsername, Long projectId, Long categoryId, int score);

    double getAverageScoreByProject(Long projectId);

    double getAverageScoreByProjectAndCategory(Long projectId, Long categoryId);

    long getSumScoreByProject(Long projectId);

    long getSumScoreByProjectAndCategory(Long projectId, Long categoryId);

    List<Vote> getVotesByUser(Long userId);

    List<Vote> getVotesByProject(Long projectId);

    List<Vote> getVotesByCategory(Long categoryId);

    List<Vote> getVotesByUserAndProject(Long userId, Long projectId);

    com.microslop.strategy.StrategyRegistry getStrategyRegistry();
}