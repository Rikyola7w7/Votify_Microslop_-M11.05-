package com.microslop.command.vote;

import com.microslop.command.AbstractCommand;
import com.microslop.entity.Competition;
import com.microslop.entity.Vote;
import com.microslop.factory.ScaleVoteCreator;
import com.microslop.repository.CategoryRepository;
import com.microslop.repository.VoteRepository;
import com.microslop.service.ProjectService;
import com.microslop.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubmitScaleVoteCommand extends AbstractCommand<Void> {

    private static final Logger log = LoggerFactory.getLogger(SubmitScaleVoteCommand.class);
    private static final int MAX_VOTES_PER_CATEGORY = 1;

    private final String userUsername;
    private final Long projectId;
    private final Long categoryId;
    private final int score;

    private final VoteRepository voteRepository;
    private final ProjectService projectService;
    private final UserService userService;
    private final ScaleVoteCreator scaleVoteCreator;
    private final CategoryRepository categoryRepository;

    private Vote createdVote;

    public SubmitScaleVoteCommand(String userUsername, Long projectId, Long categoryId, int score,
                                  VoteRepository voteRepository, ProjectService projectService,
                                  UserService userService, ScaleVoteCreator scaleVoteCreator,
                                  CategoryRepository categoryRepository) {
        this.userUsername = userUsername;
        this.projectId = projectId;
        this.categoryId = categoryId;
        this.score = score;
        this.voteRepository = voteRepository;
        this.projectService = projectService;
        this.userService = userService;
        this.scaleVoteCreator = scaleVoteCreator;
        this.categoryRepository = categoryRepository;
    }

    @Override
    protected Void executeCommand() throws Exception {
        log.debug("Submitting scale vote for user: {}, project: {}, category: {}, score: {}",
                  userUsername, projectId, categoryId, score);

        var user = userService.searchByUsernameIgnoreCase(userUsername)
                .orElseThrow(() -> new IllegalStateException("User not found: " + userUsername));

        var project = projectService.getById(projectId);
        if (project == null) {
            throw new IllegalStateException("Project not found: " + projectId);
        }

        var competition = project.getCompetition();
        if (competition == null) {
            throw new IllegalStateException("Competition not found for project: " + projectId);
        }

        if (!"SCALE".equalsIgnoreCase(competition.getVoteType())) {
            throw new IllegalStateException("This competition does not use scale voting.");
        }

        if (!competition.isActive()) {
            boolean hasEnded = competition.getEndDate() != null
                    && java.time.LocalDateTime.now().isAfter(competition.getEndDate());
            if (hasEnded) {
                throw new IllegalStateException("Esta competición ha finalizado y ya no acepta votos.");
            } else {
                throw new IllegalStateException("Esta competición está pausada temporalmente. Inténtalo más tarde.");
            }
        }

        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalStateException("Category not found: " + categoryId));

        long alreadyCastInCategory = voteRepository.countByUserIdAndCategoryId(user.getId(), category.getId());
        if (alreadyCastInCategory >= MAX_VOTES_PER_CATEGORY) {
            throw new IllegalStateException("You already voted for a project in this category.");
        }

        createdVote = scaleVoteCreator.create(user, project, category, score, competition);
        createdVote = voteRepository.save(createdVote);

        log.info("Scale vote submitted - User: {}, Project: {}, Category: {}, Score: {}, Vote ID: {}",
                 userUsername, projectId, categoryId, score, createdVote.getId());
        return null;
    }

    @Override
    public void undo() throws Exception {
        if (createdVote == null || createdVote.getId() == null) {
            throw new IllegalStateException("Cannot undo: Scale vote was not properly created");
        }
        voteRepository.deleteById(createdVote.getId());
        log.info("Scale vote undone - Vote ID: {}", createdVote.getId());
    }

    @Override
    public Void redo() throws Exception {
        if (createdVote == null) {
            throw new IllegalStateException("Cannot redo: Scale vote information was not preserved");
        }
        Vote redoneVote = new Vote(createdVote.getUser(), createdVote.getProject(),
                                   createdVote.getCategory(), createdVote.getPoints());
        redoneVote = voteRepository.save(redoneVote);
        createdVote.setId(redoneVote.getId());
        log.info("Scale vote redone - Vote ID: {}", redoneVote.getId());
        return null;
    }

    @Override
    public String getDescription() {
        return String.format("Submit scale vote from user '%s' for project %d in category %d (score: %d)",
                           userUsername, projectId, categoryId, score);
    }

    @Override
    public boolean isUndoable() {
        return true;
    }
}