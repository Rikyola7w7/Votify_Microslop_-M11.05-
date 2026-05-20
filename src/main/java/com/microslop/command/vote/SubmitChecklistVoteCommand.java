package com.microslop.command.vote;

import com.microslop.command.AbstractCommand;
import com.microslop.entity.Category;
import com.microslop.entity.ChecklistItem;
import com.microslop.entity.ChecklistVote;
import com.microslop.entity.Project;
import com.microslop.entity.User;
import com.microslop.factory.ChecklistVoteCreator;
import com.microslop.repository.CategoryRepository;
import com.microslop.repository.ChecklistItemRepository;
import com.microslop.repository.ChecklistVoteRepository;
import com.microslop.service.ProjectService;
import com.microslop.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command to submit a checklist vote for a project.
 * Supports undo/redo operations.
 */
public class SubmitChecklistVoteCommand extends AbstractCommand<Void> {

    private static final Logger log = LoggerFactory.getLogger(SubmitChecklistVoteCommand.class);

    private final String userUsername;
    private final Long projectId;
    private final Long categoryId;
    private final Long checklistItemId;

    private final ChecklistVoteRepository checklistVoteRepository;
    private final ChecklistItemRepository checklistItemRepository;
    private final CategoryRepository categoryRepository;
    private final ProjectService projectService;
    private final UserService userService;
    private final ChecklistVoteCreator checklistVoteCreator;

    private ChecklistVote createdVote;

    public SubmitChecklistVoteCommand(String userUsername, Long projectId, Long categoryId, Long checklistItemId,
                                      ChecklistVoteRepository checklistVoteRepository,
                                      ChecklistItemRepository checklistItemRepository,
                                      CategoryRepository categoryRepository,
                                      ProjectService projectService,
                                      UserService userService,
                                      ChecklistVoteCreator checklistVoteCreator) {
        this.userUsername = userUsername;
        this.projectId = projectId;
        this.categoryId = categoryId;
        this.checklistItemId = checklistItemId;
        this.checklistVoteRepository = checklistVoteRepository;
        this.checklistItemRepository = checklistItemRepository;
        this.categoryRepository = categoryRepository;
        this.projectService = projectService;
        this.userService = userService;
        this.checklistVoteCreator = checklistVoteCreator;
    }

    @Override
    protected Void executeCommand() throws Exception {
        log.debug("Submitting checklist vote for user: {}, project: {}, category: {}, item: {}",
                  userUsername, projectId, categoryId, checklistItemId);

        User user = userService.searchByUsernameIgnoreCase(userUsername)
                .orElseThrow(() -> new IllegalStateException("User not found: " + userUsername));

        Project project = projectService.getById(projectId);
        if (project == null) {
            throw new IllegalStateException("Project not found: " + projectId);
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalStateException("Category not found: " + categoryId));

        // Check if category is configured for checklist voting
        if (!category.isChecklistVoting()) {
            throw new IllegalStateException("This category does not use checklist voting.");
        }

        var competition = project.getCompetition();
        if (competition == null) {
            throw new IllegalStateException("Competition not found for project: " + projectId);
        }

        if (!competition.isActive()) {
            throw new IllegalStateException("Competition is not active.");
        }

        ChecklistItem item = checklistItemRepository.findById(checklistItemId)
                .orElseThrow(() -> new IllegalStateException("Checklist item not found: " + checklistItemId));

        if (checklistVoteRepository.existsByUserIdAndProjectIdAndChecklistItemId(
                user.getId(), projectId, checklistItemId)) {
            throw new IllegalStateException("You already checked this item for this project.");
        }

        createdVote = checklistVoteCreator.create(user, project, item);
        createdVote = checklistVoteRepository.save(createdVote);

        log.info("Checklist vote submitted - User: {}, Project: {}, Item: {}, Vote ID: {}",
                 userUsername, projectId, checklistItemId, createdVote.getId());
        return null;
    }

    @Override
    public void undo() throws Exception {
        if (createdVote == null || createdVote.getId() == null) {
            throw new IllegalStateException("Cannot undo: Checklist vote was not properly created");
        }
        checklistVoteRepository.deleteById(createdVote.getId());
        log.info("Checklist vote undone - Vote ID: {}", createdVote.getId());
    }

    @Override
    public Void redo() throws Exception {
        if (createdVote == null) {
            throw new IllegalStateException("Cannot redo: Checklist vote information was not preserved");
        }
        ChecklistVote redoneVote = new ChecklistVote(
                createdVote.getUser(), createdVote.getProject(), createdVote.getChecklistItem());
        redoneVote = checklistVoteRepository.save(redoneVote);
        createdVote.setId(redoneVote.getId());
        log.info("Checklist vote redone - Vote ID: {}", redoneVote.getId());
        return null;
    }

    @Override
    public String getDescription() {
        return String.format("Submit checklist vote from user '%s' for project %d item %d",
                           userUsername, projectId, checklistItemId);
    }

    @Override
    public boolean isUndoable() {
        return true;
    }
}
