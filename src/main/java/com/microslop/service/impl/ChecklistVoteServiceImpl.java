package com.microslop.service.impl;

import com.microslop.entity.ChecklistItem;
import com.microslop.entity.ChecklistVote;
import com.microslop.entity.Project;
import com.microslop.entity.User;
import com.microslop.exception.CompetitionStateException;
import com.microslop.exception.EntityNotFoundException;
import com.microslop.factory.ChecklistVoteCreator;
import com.microslop.repository.ChecklistItemRepository;
import com.microslop.repository.ChecklistVoteRepository;
import com.microslop.repository.ProjectRepository;
import com.microslop.service.ChecklistVoteService;
import com.microslop.service.ProjectService;
import com.microslop.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class ChecklistVoteServiceImpl implements ChecklistVoteService {

    private static final Logger log = LoggerFactory.getLogger(ChecklistVoteServiceImpl.class);

    private final ChecklistVoteRepository checklistVoteRepository;
    private final ChecklistItemRepository checklistItemRepository;
    private final ProjectService projectService;
    private final UserService userService;
    private final ChecklistVoteCreator checklistVoteCreator;
    private final ProjectRepository projectRepository;

    public ChecklistVoteServiceImpl(ChecklistVoteRepository checklistVoteRepository,
                                    ChecklistItemRepository checklistItemRepository,
                                    ProjectService projectService,
                                    UserService userService,
                                    ChecklistVoteCreator checklistVoteCreator,
                                    ProjectRepository projectRepository) {
        this.checklistVoteRepository = checklistVoteRepository;
        this.checklistItemRepository = checklistItemRepository;
        this.projectService = projectService;
        this.userService = userService;
        this.checklistVoteCreator = checklistVoteCreator;
        this.projectRepository = projectRepository;
    }

    @Override
    public void submitChecklistVote(String username, Long projectId, Long checklistItemId) {
        submitChecklistVotes(username, projectId, List.of(checklistItemId));
    }

    @Override
    @Transactional
    public void submitChecklistVotes(String username, Long projectId, List<Long> checklistItemIds) {
        User user = userService.searchByUsernameIgnoreCase(username)
                .orElseThrow(() -> new EntityNotFoundException("User", username));
        Project project = projectService.getById(projectId);
        var competition = project.getCompetition();
        if (!competition.isActive()) {
            throw new CompetitionStateException(competition.getStatus(), "vote with checklist");
        }

        if (!"CHECKLIST".equalsIgnoreCase(competition.getVoteType())) {
            throw new CompetitionStateException("This competition does not use checklist voting.");
        }

        List<ChecklistItem> items = checklistItemRepository.findAllById(checklistItemIds);
        int saved = 0;
        for (ChecklistItem item : items) {
            if (!checklistVoteRepository.existsByUserIdAndProjectIdAndChecklistItemId(
                    user.getId(), projectId, item.getId())) {
                ChecklistVote vote = checklistVoteCreator.create(user, project, item);
                checklistVoteRepository.save(vote);
                saved++;
            }
        }
        log.info("User {} submitted {} checklist votes for project {}", username, saved, projectId);
    }

    @Override
    public void removeChecklistVote(String username, Long projectId, Long checklistItemId) {
        User user = userService.searchByUsernameIgnoreCase(username)
                .orElseThrow(() -> new IllegalStateException("User not found."));

        ChecklistVote vote = checklistVoteRepository.findAll().stream()
                .filter(v -> v.getUser().getId().equals(user.getId())
                        && v.getProject().getId().equals(projectId)
                        && v.getChecklistItem().getId().equals(checklistItemId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Checklist vote not found."));

        checklistVoteRepository.delete(vote);
    }

    @Override
    @Transactional(readOnly = true)
    public long countChecklistVotesByProject(Long projectId) {
        return checklistVoteRepository.countByProjectId(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserCheckedItem(Long userId, Long projectId, Long checklistItemId) {
        return checklistVoteRepository.existsByUserIdAndProjectIdAndChecklistItemId(
                userId, projectId, checklistItemId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countCheckedItemsByUserForProject(Long userId, Long projectId) {
        return checklistVoteRepository.countByUserIdAndProjectId(userId, projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserVotedAllProjects(Long userId, Long competitionId) {
        var projects = projectRepository.findByCompetitionId(competitionId);
        var items = checklistItemRepository.findByCompetitionId(competitionId);
        if (projects.isEmpty() || items.isEmpty()) {
            return true; // Nothing to vote on
        }
        for (Project project : projects) {
            long checked = checklistVoteRepository.countByUserIdAndProjectId(userId, project.getId());
            if (checked < items.size()) {
                return false;
            }
        }
        return true;
    }
}
