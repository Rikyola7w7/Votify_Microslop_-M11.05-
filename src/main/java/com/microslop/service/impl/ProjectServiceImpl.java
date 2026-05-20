package com.microslop.service.impl;

import com.microslop.entity.Project;
import com.microslop.repository.ProjectRepository;
import com.microslop.service.ProjectService;
import com.microslop.specification.project.ProjectsByCompetitionSpecification;
import com.microslop.specification.project.ProjectsByCreatorSpecification;
import com.microslop.command.CommandExecutor;
import com.microslop.command.project.CreateProjectCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final CommandExecutor commandExecutor;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                            CommandExecutor commandExecutor) {
        this.projectRepository = projectRepository;
        this.commandExecutor = commandExecutor;
    }

    // ── Write Operations ────────────────────────────────────────────────────────────

    @Override
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @Override
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
        if (!anyCustomPosition) {
            return baseRanking;
        }

        Map<Long, Integer> baseOrder = new HashMap<>();
        for (int i = 0; i < baseRanking.size(); i++) {
            baseOrder.put(baseRanking.get(i).getId(), i);
        }

        List<Project> sorted = new ArrayList<>(baseRanking);
        sorted.sort((a, b) -> {
            Integer posA = a.getCustomPosition();
            Integer posB = b.getCustomPosition();
            if (posA != null && posB != null) return Integer.compare(posA, posB);
            if (posA != null) return -1;
            if (posB != null) return 1;
            return Integer.compare(
                    baseOrder.getOrDefault(a.getId(), Integer.MAX_VALUE),
                    baseOrder.getOrDefault(b.getId(), Integer.MAX_VALUE)
            );
        });

        return sorted;
    }

    @Override
    public void reclassifyProject(Long projectId, int newPosition) {
        if (newPosition < 1) {
            throw new IllegalArgumentException("Position must be at least 1");
        }
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        project.setCustomPosition(newPosition);
        projectRepository.save(project);
    }

    @Override
    public void declassifyProject(Long projectId) {
        projectRepository.deleteById(projectId);
    }

    @Override
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
    @Transactional(readOnly = true)
    public List<Project> getUserProjects(String username) {
        List<Project> projects = projectRepository.findProjectsByParticipantUsername(username);
        projects.forEach(p -> {
            if (p.getCompetition() != null) {
                p.getCompetition().getName();
            }
            if (p.getVotes() != null) {
                p.getVotes().size();
            }
        });
        return projects;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> getUserProjectsByUserId(Long userId) {
        return projectRepository.findAll(new ProjectsByCreatorSpecification(userId));
    }
}
