package com.microslop.service.impl;

import com.microslop.entity.Project;
import com.microslop.repository.ProjectRepository;
import com.microslop.service.ProjectService;
import com.microslop.command.CommandExecutor;
import com.microslop.command.project.CreateProjectCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

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
        // Use custom query to fetch project with votes and users to avoid lazy loading issues
        return projectRepository.findByIdWithVotesAndUsers(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> listByCompetition(Long competitionId) {
        return projectRepository.findByCompetitionId(competitionId);
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
    public List<Project> getUserProjects(String username) {
        List<Project> projects = projectRepository.findProjectsByParticipantUsername(username);
        // Access all fields within transaction to prevent lazy loading errors
        projects.forEach(p -> {
            // Access competition and votes
            if (p.getCompetition() != null) {
                p.getCompetition().getName();
            }
            if (p.getVotes() != null) {
                p.getVotes().size();
            }
        });
        return projects;
    }
}
