package com.microslop.command.vote;

import com.microslop.command.AbstractCommand;
import com.microslop.entity.Vote;
import com.microslop.repository.VoteRepository;
import com.microslop.repository.CategoryRepository;
import com.microslop.service.ProjectService;
import com.microslop.service.UserService;
import com.microslop.factory.VoteCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command to submit a vote for a project in a specific category.
 * This command encapsulates the vote submission logic and supports undo/redo operations.
 *
 * The command will validate that:
 * - The user exists
 * - The project exists
 * - The category exists
 * - The competition is active
 * - The user has not already voted in this category
 *
 * @see AbstractCommand
 */
public class SubmitVoteCommand extends AbstractCommand<Void> {

    private static final Logger log = LoggerFactory.getLogger(SubmitVoteCommand.class);
    private static final int MAX_VOTES_PER_CATEGORY = 1;

    private final String userUsername;
    private final Long projectId;
    private final Long categoryId;
    private final Integer points;

    private final VoteRepository voteRepository;
    private final ProjectService projectService;
    private final UserService userService;
    private final VoteCreator voteCreator;
    private final CategoryRepository categoryRepository;

    // Store the created vote for undo operation
    private Vote createdVote;

    /**
     * Creates a new SubmitVoteCommand with default points (1).
     *
     * @param userUsername the username of the user submitting the vote
     * @param projectId the ID of the project being voted for
     * @param categoryId the ID of the category for this vote
     * @param voteRepository the repository to persist votes
     * @param projectService the service to retrieve project information
     * @param userService the service to retrieve user information
     * @param voteCreator the factory to create votes
     * @param categoryRepository the repository to retrieve category information
     */
    public SubmitVoteCommand(String userUsername, Long projectId, Long categoryId,
                            VoteRepository voteRepository, ProjectService projectService,
                            UserService userService, VoteCreator voteCreator,
                            CategoryRepository categoryRepository) {
        this(userUsername, projectId, categoryId, 1, voteRepository, projectService, 
             userService, voteCreator, categoryRepository);
    }

    /**
     * Creates a new SubmitVoteCommand with a specific number of points.
     *
     * @param userUsername the username of the user submitting the vote
     * @param projectId the ID of the project being voted for
     * @param categoryId the ID of the category for this vote
     * @param points the number of points to assign to this vote
     * @param voteRepository the repository to persist votes
     * @param projectService the service to retrieve project information
     * @param userService the service to retrieve user information
     * @param voteCreator the factory to create votes
     * @param categoryRepository the repository to retrieve category information
     */
    public SubmitVoteCommand(String userUsername, Long projectId, Long categoryId, Integer points,
                            VoteRepository voteRepository, ProjectService projectService,
                            UserService userService, VoteCreator voteCreator,
                            CategoryRepository categoryRepository) {
        this.userUsername = userUsername;
        this.projectId = projectId;
        this.categoryId = categoryId;
        this.points = points != null ? points : 1;
        this.voteRepository = voteRepository;
        this.projectService = projectService;
        this.userService = userService;
        this.voteCreator = voteCreator;
        this.categoryRepository = categoryRepository;
    }

    /**
     * {@inheritDoc}
     * Executes the vote submission with all necessary validations.
     */
    @Override
    protected Void executeCommand() throws Exception {
        log.debug("Submitting vote for user: {}, project: {}, category: {}", 
                  userUsername, projectId, categoryId);

        // Validate and retrieve user
        var user = userService.searchByUsernameIgnoreCase(userUsername)
                .orElseThrow(() -> new IllegalStateException("User not found: " + userUsername));

        // Validate and retrieve project
        var project = projectService.getById(projectId);
        if (project == null) {
            throw new IllegalStateException("Project not found: " + projectId);
        }

        // Validate and retrieve competition
        var competition = project.getCompetition();
        if (competition == null) {
            throw new IllegalStateException("Competition not found for project: " + projectId);
        }

        if ("CHECKLIST".equalsIgnoreCase(competition.getVoteType())) {
            throw new IllegalStateException("This competition uses checklist voting. Please use the checklist voting interface.");
        }

        // Validate competition is active
        if (!competition.isActive()) {
            boolean hasEnded = competition.getEndDate() != null
                    && java.time.LocalDateTime.now().isAfter(competition.getEndDate());
            if (hasEnded) {
                throw new IllegalStateException(
                    "Esta competición ha finalizado y ya no acepta votos.");
            } else {
                throw new IllegalStateException(
                    "Esta competición está pausada temporalmente. Inténtalo más tarde.");
            }
        }

        // Validate and retrieve category
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalStateException("Category not found: " + categoryId));

        // Check if user has already voted in this category
        long alreadyCastInCategory = voteRepository.countByUserIdAndCategoryId(
            user.getId(), category.getId());
        if (alreadyCastInCategory >= MAX_VOTES_PER_CATEGORY) {
            throw new IllegalStateException(
                "You already voted for a project in this category.");
        }

        // Create and persist the vote
        if (points > 1) {
            createdVote = new Vote(user, project, category, points);
        } else {
            createdVote = voteCreator.create(user, project, category);
        }
        
        createdVote = voteRepository.save(createdVote);
        log.info("Vote successfully submitted - User: {}, Project: {}, Category: {}, Vote ID: {}", 
                 userUsername, projectId, categoryId, createdVote.getId());

        return null;
    }

    /**
     * {@inheritDoc}
     * Undoes the vote submission by deleting the created vote from the repository.
     */
    @Override
    public void undo() throws Exception {
        if (createdVote == null || createdVote.getId() == null) {
            throw new IllegalStateException("Cannot undo: Vote was not properly created or ID is missing");
        }

        log.debug("Undoing vote submission - Vote ID: {}", createdVote.getId());
        voteRepository.deleteById(createdVote.getId());
        log.info("Vote successfully undone - Vote ID: {}", createdVote.getId());
    }

    /**
     * {@inheritDoc}
     * Redoes the vote submission by recreating and persisting the vote.
     */
    @Override
    public Void redo() throws Exception {
        if (createdVote == null) {
            throw new IllegalStateException("Cannot redo: Vote information was not preserved");
        }

        log.debug("Redoing vote submission - User: {}, Project: {}, Category: {}", 
                  userUsername, projectId, categoryId);
        
        // Recreate the vote with the same data
        Vote redoneVote = new Vote(createdVote.getUser(), createdVote.getProject(), 
                                    createdVote.getCategory(), createdVote.getPoints());
        redoneVote = voteRepository.save(redoneVote);
        createdVote.setId(redoneVote.getId()); // Update ID for future undo/redo cycles
        
        log.info("Vote successfully redone - Vote ID: {}", redoneVote.getId());
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return String.format("Submit vote from user '%s' for project %d in category %d", 
                           userUsername, projectId, categoryId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUndoable() {
        return true;
    }
}
