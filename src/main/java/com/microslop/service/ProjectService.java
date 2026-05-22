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

    /**
     * Get ranking for a category with custom position and manual vote count overrides applied.
     * @param categoryId the category ID
     * @param isJudgesRanking whether to use judge or popular ranking as base
     * @return ordered list of projects
     */
    java.util.List<Project> getRankingForCategory(Long categoryId, boolean isJudgesRanking);

    /**
     * Get projects for a competition that belong to a specific category, with categories eagerly loaded.
     * @param competitionId the competition ID
     * @param categoryId the category ID to filter by
     * @return list of projects in that category
     */
    java.util.List<Project> listByCompetitionWithCategories(Long competitionId, Long categoryId);

    /**
     * Reclassify a project to a new position in the ranking.
     * @param projectId the project ID
     * @param newPosition the desired position (1-indexed)
     */
    void reclassifyProject(Long projectId, int newPosition);

    /**
     * Delete a project from the competition entirely.
     * @param projectId the project ID
     */
    void declassifyProject(Long projectId);

    /**
     * Set a manual vote count for a project, overriding the actual vote count.
     * @param projectId the project ID
     * @param newVoteCount the new vote count
     */
    void editProjectVotes(Long projectId, int newVoteCount);

    /**
     * Reset all custom positions and manual vote counts for projects in a competition,
     * restoring the natural ranking order.
     * @param competitionId the competition ID
     */
    void resetAllModifications(Long competitionId);
}