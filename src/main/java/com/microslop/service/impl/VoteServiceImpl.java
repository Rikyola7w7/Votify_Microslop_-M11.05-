package com.microslop.service.impl;

import com.microslop.entity.Vote;
import com.microslop.factory.VoteCreator;
import com.microslop.repository.VoteRepository;
import com.microslop.repository.CategoryRepository;
import com.microslop.service.ProjectService;
import com.microslop.service.UserService;
import com.microslop.service.VoteService;
import com.microslop.command.CommandExecutor;
import com.microslop.command.vote.SubmitVoteCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VoteServiceImpl implements VoteService {

    private static final int MAX_VOTES_PER_CATEGORY = 1;

    private final VoteRepository voteRepository;
    private final ProjectService projectService;
    private final UserService    userService;
    private final VoteCreator voteCreator;
    private final CategoryRepository categoryRepository;
    private final CommandExecutor commandExecutor;

    public VoteServiceImpl(VoteRepository voteRepository,
                           ProjectService projectService,
                           UserService userService,
                           VoteCreator voteCreator,
                           CategoryRepository categoryRepository,
                           CommandExecutor commandExecutor) {
        this.voteRepository = voteRepository;
        this.projectService = projectService;
        this.userService    = userService;
        this.voteCreator    = voteCreator;
        this.categoryRepository = categoryRepository;
        this.commandExecutor = commandExecutor;
    }

    // ── Write ─────────────────────────────────────────────────────────────

    @Override
    public void submitVote(String userUsername, Long projectId, Long categoryId) {
        // Execute command through command executor
        SubmitVoteCommand command = new SubmitVoteCommand(
            userUsername, projectId, categoryId,
            voteRepository, projectService, userService, voteCreator, categoryRepository
        );
        try {
            commandExecutor.execute(command);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to submit vote", e);
        }
    }

    @Override
    public void submitVote(String userUsername, Long projectId, Long categoryId, int points) {
        // Execute command through command executor
        SubmitVoteCommand command = new SubmitVoteCommand(
            userUsername, projectId, categoryId, points,
            voteRepository, projectService, userService, voteCreator, categoryRepository
        );
        try {
            commandExecutor.execute(command);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to submit vote with points", e);
        }
    }

    // ── Read ──────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public long countVotesByProject(Long projectId) {
        return voteRepository.countByProjectId(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countVotesByProjectAndCategory(Long projectId, Long categoryId) {
        return voteRepository.countByProjectIdAndCategoryId(projectId, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countVotesByUserAndProject(Long userId, Long projectId) {
        return voteRepository.countByUserIdAndProjectId(userId, projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countVotesPerUserInCompetition(Long userId, Long competitionId) {
        return voteRepository.countByUserInCompetition(userId, competitionId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countVotesByUserAndCategory(Long userId, Long categoryId) {
        return voteRepository.countByUserIdAndCategoryId(userId, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countVotesByUserAndProjectAndCategory(Long userId, Long projectId, Long categoryId) {
        return voteRepository.countByUserIdAndProjectIdAndCategoryId(userId, projectId, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPointsByUserAndCategory(Long userId, Long categoryId) {
        return voteRepository.sumPointsByUserIdAndCategoryId(userId, categoryId);
    }
}