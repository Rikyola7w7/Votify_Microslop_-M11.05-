package com.microslop.service.impl;

import com.microslop.entity.Judge;
import com.microslop.entity.Project;
import com.microslop.repository.CompetitionRepository;
import com.microslop.repository.JudgeRepository;
import com.microslop.repository.ProjectRepository;
import com.microslop.service.ProjectService;
import com.microslop.specification.project.ProjectsByCompetitionSpecification;
import com.microslop.specification.project.ProjectsByCreatorSpecification;
import com.microslop.command.CommandExecutor;
import com.microslop.command.project.CreateProjectCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final CompetitionRepository competitionRepository;
    private final CommandExecutor commandExecutor;
    private final JudgeRepository judgeRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                              CompetitionRepository competitionRepository,
                              CommandExecutor commandExecutor,
                              JudgeRepository judgeRepository) {
        this.projectRepository = projectRepository;
        this.competitionRepository = competitionRepository;
        this.commandExecutor = commandExecutor;
        this.judgeRepository = judgeRepository;
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
        var competition = competitionRepository.findById(competitionId).orElse(null);
        if (competition != null && "CHECKLIST".equalsIgnoreCase(competition.getVoteType())) {
            return projectRepository.findRankingByChecklistCompetition(competitionId);
        }
        if (competition != null && "SCALE".equalsIgnoreCase(competition.getVoteType())) {
            return projectRepository.findRankingByScaleCompetition(competitionId);
        }
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
    public List<Project> getChecklistRankingByCategory(Long categoryId) {
        return projectRepository.findChecklistRankingByCategory(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> getRankingForCategory(Long categoryId, boolean isJudgesRanking) {
        // Get ALL projects in this category (including zero-vote projects)
        List<Project> allProjects = projectRepository.findAllByCategoryId(categoryId);

        if (allProjects.isEmpty()) {
            return allProjects;
        }

        // Get judge user IDs for the competition to determine vote type classification
        Long competitionId = allProjects.get(0).getCompetition().getId();
        Set<Long> judgeUserIds = judgeRepository.findByCompetitionId(competitionId)
                .stream()
                .map(j -> j.getUser().getId())
                .collect(Collectors.toSet());

        // Count relevant votes per project (judge or popular votes)
        Map<Long, Long> relevantVoteCounts = new HashMap<>();
        for (Project p : allProjects) {
            long count = 0;
            if (p.getVotes() != null) {
                count = p.getVotes().stream()
                        .filter(v -> v != null && v.getUser() != null)
                        .filter(v -> isJudgesRanking == judgeUserIds.contains(v.getUser().getId()))
                        .count();
            }
            relevantVoteCounts.put(p.getId(), count);
        }

        // Separate projects with and without custom position
        List<Project> withPosition = new ArrayList<>();
        List<Project> withoutPosition = new ArrayList<>();
        for (Project p : allProjects) {
            if (p.getCustomPosition() != null) {
                withPosition.add(p);
            } else {
                withoutPosition.add(p);
            }
        }

        // Sort projects with custom position by their position (ascending)
        withPosition.sort((a, b) -> {
            int cmp = Integer.compare(a.getCustomPosition(), b.getCustomPosition());
            if (cmp != 0) return cmp;
            return Long.compare(a.getId(), b.getId());
        });

        // Sort projects without custom position by votes (manual override else relevant), descending
        withoutPosition.sort((a, b) -> {
            long votesA = a.getManualVoteCount() != null
                    ? a.getManualVoteCount()
                    : relevantVoteCounts.getOrDefault(a.getId(), 0L);
            long votesB = b.getManualVoteCount() != null
                    ? b.getManualVoteCount()
                    : relevantVoteCounts.getOrDefault(b.getId(), 0L);
            if (votesA != votesB) return Long.compare(votesB, votesA);
            return Long.compare(a.getId(), b.getId());
        });

        // Insert projects with custom position at their positions (insert semantics)
        List<Project> result = new ArrayList<>(withoutPosition);
        for (Project p : withPosition) {
            int pos = Math.min(p.getCustomPosition() - 1, result.size());
            result.add(pos, p);
        }
        return result;
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

    @Override
    public void clearAllModifications(Long categoryId) {
        projectRepository.clearModificationsByCategoryId(categoryId);
    }
}
