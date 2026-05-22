package com.microslop.service.impl;

import com.microslop.entity.Project;
import com.microslop.repository.ProjectRepository;
import com.microslop.repository.VoteRepository;
import com.microslop.service.ProjectService;
import com.microslop.specification.project.ProjectsByCompetitionSpecification;
import com.microslop.specification.project.ProjectsByCreatorSpecification;
import com.microslop.command.CommandExecutor;
import com.microslop.command.project.CreateProjectCommand;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final VoteRepository voteRepository;
    private final CommandExecutor commandExecutor;

    @PersistenceContext
    private EntityManager entityManager;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                            VoteRepository voteRepository,
                            CommandExecutor commandExecutor) {
        this.projectRepository = projectRepository;
        this.voteRepository = voteRepository;
        this.commandExecutor = commandExecutor;
    }

    // ── Write Operations ────────────────────────────────────────────────────────────

    @Override
    @Transactional
    @CacheEvict(value = {"projects", "projectsAll"}, allEntries = true)
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"projects", "projectsAll"}, allEntries = true)
    public void delete(Long id) {
        projectRepository.deleteById(id);
    }

    // ── Read Operations ────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Project getById(Long id) {
        return projectRepository.findByIdWithVotesAndUsers(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> listByCompetition(Long competitionId) {
        return projectRepository.findAll(new ProjectsByCompetitionSpecification(competitionId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> getRanking(Long competitionId) {
        return projectRepository.findRankingByCompetition(competitionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> getRankingByCategory(Long categoryId) {
        return projectRepository.findRankingByCategory(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> getJudgeRankingByCategory(Long categoryId) {
        return projectRepository.findJudgeRankingByCategory(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> getPopularRankingByCategory(Long categoryId) {
        return projectRepository.findPopularRankingByCategory(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> getRankingForCategory(Long categoryId, boolean isJudgesRanking) {
        List<Project> baseRanking = isJudgesRanking
                ? projectRepository.findJudgeRankingByCategory(categoryId)
                : projectRepository.findPopularRankingByCategory(categoryId);

        boolean anyCustomPosition = baseRanking.stream().anyMatch(p -> p.getCustomPosition() != null);
        boolean anyManualVoteCount = baseRanking.stream().anyMatch(p -> p.getManualVoteCount() != null);

        if (!anyCustomPosition && !anyManualVoteCount) {
            return baseRanking;
        }

        Map<Long, Integer> baseOrder = new HashMap<>();
        for (int i = 0; i < baseRanking.size(); i++) {
            baseOrder.put(baseRanking.get(i).getId(), i);
        }

        List<Long> projectIds = baseRanking.stream().map(Project::getId).toList();
        Map<Long, Long> batchCounts = voteRepository.countVotesByProjectIds(projectIds).stream()
                .collect(java.util.stream.Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        Map<Long, Integer> effectiveVoteCounts = new HashMap<>();
        for (Project p : baseRanking) {
            int count = p.getManualVoteCount() != null
                    ? p.getManualVoteCount()
                    : batchCounts.getOrDefault(p.getId(), 0L).intValue();
            effectiveVoteCounts.put(p.getId(), count);
        }

        List<Project> sorted = new ArrayList<>(baseRanking);
        sorted.sort((a, b) -> {
            Integer posA = a.getCustomPosition();
            Integer posB = b.getCustomPosition();
            if (posA != null && posB != null) {
                if (!posA.equals(posB)) return Integer.compare(posA, posB);
            } else if (posA != null) {
                return -1;
            } else if (posB != null) {
                return 1;
            }

            int votesA = effectiveVoteCounts.getOrDefault(a.getId(), 0);
            int votesB = effectiveVoteCounts.getOrDefault(b.getId(), 0);
            if (votesA != votesB) return Integer.compare(votesB, votesA);

            return Integer.compare(
                    baseOrder.getOrDefault(a.getId(), Integer.MAX_VALUE),
                    baseOrder.getOrDefault(b.getId(), Integer.MAX_VALUE)
            );
        });

        return sorted;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"projects", "projectsAll"}, allEntries = true)
    public void reclassifyProject(Long projectId, int newPosition) {
        if (newPosition < 1) {
            throw new IllegalArgumentException("Position must be at least 1");
        }
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        List<Project> projectsWithCustomPosition = projectRepository
                .findByCompetitionIdAndCustomPositionIsNotNull(project.getCompetition().getId());
        projectsWithCustomPosition.removeIf(p -> p.getId().equals(projectId));

        for (Project p : projectsWithCustomPosition) {
            if (p.getCustomPosition() >= newPosition) {
                p.setCustomPosition(p.getCustomPosition() + 1);
            }
        }
        projectRepository.saveAll(projectsWithCustomPosition);

        project.setCustomPosition(newPosition);
        projectRepository.save(project);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"projects", "projectsAll"}, allEntries = true)
    public void declassifyProject(Long projectId) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project != null && project.getCustomPosition() != null) {
            List<Project> projectsWithCustomPosition = projectRepository
                    .findByCompetitionIdAndCustomPositionIsNotNull(project.getCompetition().getId());
            projectsWithCustomPosition.removeIf(p -> p.getId().equals(projectId));
            for (Project p : projectsWithCustomPosition) {
                if (p.getCustomPosition() > project.getCustomPosition()) {
                    p.setCustomPosition(p.getCustomPosition() - 1);
                }
            }
            projectRepository.saveAll(projectsWithCustomPosition);
        }

        entityManager.createNativeQuery("DELETE FROM ai_feedback WHERE project_id = :projectId")
                .setParameter("projectId", projectId)
                .executeUpdate();
        projectRepository.deleteById(projectId);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"projects", "projectsAll"}, allEntries = true)
    public void editProjectVotes(Long projectId, int newVoteCount) {
        if (newVoteCount < 0) {
            throw new IllegalArgumentException("Vote count cannot be negative");
        }
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        project.setManualVoteCount(newVoteCount);
        projectRepository.save(project);
    }

    @Override
    public void resetAllModifications(Long competitionId) {
        List<Project> projects = projectRepository.findByCompetitionId(competitionId);
        for (Project p : projects) {
            p.setCustomPosition(null);
            p.setManualVoteCount(null);
        }
        projectRepository.saveAll(projects);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> getUserProjects(String username) {
        return projectRepository.findProjectsByParticipantUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> getUserProjectsByUserId(Long userId) {
        return projectRepository.findAll(new ProjectsByCreatorSpecification(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> listByCompetitionWithCategories(Long competitionId, Long categoryId) {
        return projectRepository.findByCompetitionIdAndCategoryId(competitionId, categoryId);
    }
}