package com.microslop.service;

import com.microslop.entity.Project;

public interface ProjectService {
    
    Project save(Project project);

    void delete(Long id);

    Project getById(Long id);

    java.util.List<Project> listByCompetition(Long competitionId);

    java.util.List<Project> getRanking(Long competitionId);

    java.util.List<Project> getRankingByCategory(Long categoryId);

    java.util.List<Project> getJudgeRankingByCategory(Long categoryId);

    java.util.List<Project> getPopularRankingByCategory(Long categoryId);

    java.util.List<Project> getUserProjects(String username);

    /**
     * Get projects where a user is a participant, using Specification pattern.
     * @param userId the user ID
     * @return list of projects where the user is a participant
     */
    java.util.List<Project> getUserProjectsByUserId(Long userId);
}