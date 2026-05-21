package com.microslop.command.vote;

import com.microslop.command.AbstractCommand;
import com.microslop.entity.Vote;
import com.microslop.entity.Competition;
import com.microslop.entity.Voter;
import com.microslop.repository.VoteRepository;
import com.microslop.repository.VoterRepository;
import com.microslop.repository.CategoryRepository;
import com.microslop.service.ProjectService;
import com.microslop.service.UserService;
import com.microslop.strategy.StrategyRegistry;
import com.microslop.strategy.voting.VotingStrategy;
import com.microslop.factory.VoteCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubmitVoteCommand extends AbstractCommand<Void> {

    private static final Logger log = LoggerFactory.getLogger(SubmitVoteCommand.class);

    private final String userUsername;
    private final Long projectId;
    private final Long categoryId;
    private final Integer points;

    private final VoteRepository voteRepository;
    private final ProjectService projectService;
    private final UserService userService;
    private final VoteCreator voteCreator;
    private final CategoryRepository categoryRepository;
    private final VoterRepository voterRepository;
    private final StrategyRegistry strategyRegistry;

    private Vote createdVote;

    public SubmitVoteCommand(String userUsername, Long projectId, Long categoryId,
                            VoteRepository voteRepository, ProjectService projectService,
                            UserService userService, VoteCreator voteCreator,
                            CategoryRepository categoryRepository, VoterRepository voterRepository,
                            StrategyRegistry strategyRegistry) {
        this(userUsername, projectId, categoryId, 1, voteRepository, projectService,
             userService, voteCreator, categoryRepository, voterRepository, strategyRegistry);
    }

    public SubmitVoteCommand(String userUsername, Long projectId, Long categoryId, Integer points,
                            VoteRepository voteRepository, ProjectService projectService,
                            UserService userService, VoteCreator voteCreator,
                            CategoryRepository categoryRepository, VoterRepository voterRepository,
                            StrategyRegistry strategyRegistry) {
        this.userUsername = userUsername;
        this.projectId = projectId;
        this.categoryId = categoryId;
        this.points = points != null ? points : 1;
        this.voteRepository = voteRepository;
        this.projectService = projectService;
        this.userService = userService;
        this.voteCreator = voteCreator;
        this.categoryRepository = categoryRepository;
        this.voterRepository = voterRepository;
        this.strategyRegistry = strategyRegistry;
    }

    @Override
    protected Void executeCommand() throws Exception {
        log.debug("Submitting vote for user: {}, project: {}, category: {}",
                  userUsername, projectId, categoryId);

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

if ("CHECKLIST".equalsIgnoreCase(competition.getVoteType())) {
            throw new IllegalStateException("This competition uses checklist voting. Please use the checklist voting interface.");
        }

        VotingStrategy votingStrategy = strategyRegistry.resolveVotingStrategy(competition.getVotingStrategyType());

        if (!votingStrategy.canVote(user, competition)) {
            throw new IllegalStateException("User is not allowed to vote in this competition");
        }
        if (!competition.isActive()) {
            boolean hasEnded = competition.getEndDate() != null
                    && java.time.LocalDateTime.now().isAfter(competition.getEndDate());
            if (hasEnded) {
                throw new IllegalStateException(
                    "This competition has ended and no longer accepts votes.");
            } else {
                throw new IllegalStateException(
                    "This competition is currently paused. Try again later.");
            }
        }

        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalStateException("Category not found: " + categoryId));

        // Check votes_left from Voter record
        var voter = voterRepository.findByUserIdAndCompetitionIdAndCategoryId(
                user.getId(), competition.getId(), categoryId)
                .orElseThrow(() -> new IllegalStateException(
                    "You are not registered as a voter for this category."));

        if (voter.getVotesLeft() < points) {
            throw new IllegalStateException(
                "You don't have enough votes left. You have " + voter.getVotesLeft() + " vote(s) remaining.");
        }

        int effectivePoints = votingStrategy.calculateVotePoints(user, competition, this.points);

        if (effectivePoints > 1) {
            createdVote = new Vote(user, project, category, effectivePoints);
        } else {
            createdVote = voteCreator.create(user, project, category);
        }

        createdVote = voteRepository.save(createdVote);
        log.info("Vote successfully submitted - User: {}, Project: {}, Category: {}, Vote ID: {}",
                 userUsername, projectId, categoryId, createdVote.getId());

        return null;
    }

    @Override
    public void undo() throws Exception {
        if (createdVote == null || createdVote.getId() == null) {
            throw new IllegalStateException("Cannot undo: Vote was not properly created or ID is missing");
        }

        log.debug("Undoing vote submission - Vote ID: {}", createdVote.getId());
        voteRepository.deleteById(createdVote.getId());
        log.info("Vote successfully undone - Vote ID: {}", createdVote.getId());
    }

    @Override
    public Void redo() throws Exception {
        if (createdVote == null) {
            throw new IllegalStateException("Cannot redo: Vote information was not preserved");
        }

        log.debug("Redoing vote submission - User: {}, Project: {}, Category: {}",
                  userUsername, projectId, categoryId);

        Vote redoneVote = new Vote(createdVote.getUser(), createdVote.getProject(),
                                    createdVote.getCategory(), createdVote.getPoints());
        redoneVote = voteRepository.save(redoneVote);
        createdVote.setId(redoneVote.getId());

        log.info("Vote successfully redone - Vote ID: {}", redoneVote.getId());
        return null;
    }

    @Override
    public String getDescription() {
        return String.format("Submit vote from user '%s' for project %d in category %d",
                           userUsername, projectId, categoryId);
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    public Vote getCreatedVote() {
        return createdVote;
    }
}
