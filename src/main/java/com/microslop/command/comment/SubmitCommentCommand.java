package com.microslop.command.comment;

import com.microslop.command.AbstractCommand;
import com.microslop.entity.ProjectComment;
import com.microslop.repository.ProjectCommentRepository;
import com.microslop.repository.ProjectRepository;
import com.microslop.repository.UserRepository;
import com.microslop.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command to submit a comment on a project.
 * This command encapsulates the comment submission logic and supports undo/redo operations.
 *
 * The command will validate that:
 * - The user exists
 * - The project exists
 * - The category exists
 * - The comment text is not empty
 *
 * @see AbstractCommand
 */
public class SubmitCommentCommand extends AbstractCommand<Void> {

    private static final Logger log = LoggerFactory.getLogger(SubmitCommentCommand.class);

    private final String userUsername;
    private final Long projectId;
    private final Long categoryId;
    private final String commentText;

    private final ProjectCommentRepository commentRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    // Store the created comment for undo operation
    private ProjectComment createdComment;

    /**
     * Creates a new SubmitCommentCommand.
     *
     * @param userUsername the username of the user submitting the comment
     * @param projectId the ID of the project being commented on
     * @param categoryId the ID of the category for this comment
     * @param commentText the text of the comment
     * @param commentRepository the repository to persist comments
     * @param projectRepository the repository to retrieve project information
     * @param userRepository the repository to retrieve user information
     * @param categoryRepository the repository to retrieve category information
     */
    public SubmitCommentCommand(String userUsername, Long projectId, Long categoryId, String commentText,
                               ProjectCommentRepository commentRepository, ProjectRepository projectRepository,
                               UserRepository userRepository, CategoryRepository categoryRepository) {
        this.userUsername = userUsername;
        this.projectId = projectId;
        this.categoryId = categoryId;
        this.commentText = commentText;
        this.commentRepository = commentRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * {@inheritDoc}
     * Executes the comment submission with all necessary validations.
     */
    @Override
    protected Void executeCommand() throws Exception {
        log.debug("Submitting comment for user: {}, project: {}, category: {}", 
                  userUsername, projectId, categoryId);

        // Validate input
        if (commentText == null || commentText.isBlank()) {
            throw new IllegalArgumentException("Comment text cannot be null or blank");
        }

        // Validate and retrieve user
        var user = userRepository.findByUsernameIgnoreCase(userUsername)
                .orElseThrow(() -> new IllegalStateException("User not found: " + userUsername));

        // Validate and retrieve project
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalStateException("Project not found: " + projectId));

        // Validate and retrieve category
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalStateException("Category not found: " + categoryId));

        // Check if comments are enabled for this competition
        if (project.getCompetition() != null && !project.getCompetition().getCommentsEnabled()) {
            throw new IllegalStateException("Comments are disabled for this competition");
        }

        // Create and persist the comment
        createdComment = new ProjectComment(project, user, commentText, category);
        createdComment = commentRepository.save(createdComment);

        log.info("Comment successfully submitted - User: {}, Project: {}, Comment ID: {}", 
                 userUsername, projectId, createdComment.getId());

        return null;
    }

    /**
     * {@inheritDoc}
     * Undoes the comment submission by deleting the created comment from the repository.
     */
    @Override
    public void undo() throws Exception {
        if (createdComment == null || createdComment.getId() == null) {
            throw new IllegalStateException("Cannot undo: Comment was not properly created or ID is missing");
        }

        log.debug("Undoing comment submission - Comment ID: {}", createdComment.getId());
        commentRepository.deleteById(createdComment.getId());
        log.info("Comment successfully undone - Comment ID: {}", createdComment.getId());
    }

    /**
     * {@inheritDoc}
     * Redoes the comment submission by recreating and persisting the comment.
     */
    @Override
    public Void redo() throws Exception {
        if (createdComment == null) {
            throw new IllegalStateException("Cannot redo: Comment information was not preserved");
        }

        log.debug("Redoing comment submission - User: {}, Project: {}", 
                  userUsername, projectId);

        // Recreate the comment with the same data
        ProjectComment redoneComment = new ProjectComment(
            createdComment.getProject(),
            createdComment.getUser(),
            createdComment.getCommentText(),
            createdComment.getCategory()
        );

        redoneComment = commentRepository.save(redoneComment);
        createdComment.setId(redoneComment.getId()); // Update ID for future undo/redo cycles

        log.info("Comment successfully redone - Comment ID: {}", redoneComment.getId());
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return String.format("Submit comment from user '%s' on project %d", userUsername, projectId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUndoable() {
        return true;
    }
}
