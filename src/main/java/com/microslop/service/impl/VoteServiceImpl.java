package com.microslop.service.impl;

import com.microslop.entity.Vote;
import com.microslop.factory.VoteFactory;
import com.microslop.repository.VoteRepository;
import com.microslop.repository.CategoryRepository;
import com.microslop.service.ProjectService;
import com.microslop.service.UserService;
import com.microslop.service.VoteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VoteServiceImpl implements VoteService {

    private static final int MAX_VOTES_PER_CATEGORY = 1;

    private final VoteRepository voteRepository;
    private final ProjectService projectService;
    private final UserService    userService;
    private final VoteFactory    voteFactory;
    private final CategoryRepository categoryRepository;

    public VoteServiceImpl(VoteRepository voteRepository,
                           ProjectService projectService,
                           UserService userService,
                           VoteFactory voteFactory,
                           CategoryRepository categoryRepository) {
        this.voteRepository = voteRepository;
        this.projectService = projectService;
        this.userService    = userService;
        this.voteFactory    = voteFactory;
        this.categoryRepository = categoryRepository;
    }

    // ── Write ─────────────────────────────────────────────────────────────

    @Override
    public void submitVote(String userUsername, Long projectId, Long categoryId) {
        var user        = userService.searchByUsernameIgnoreCase(userUsername)
                            .orElseThrow(() -> new IllegalStateException("User not found."));
        var project     = projectService.getById(projectId);
        var competition = project.getCompetition();
        var category    = categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new IllegalStateException("Category not found."));

        if (!competition.isActive()) {
            boolean hasEnded = competition.getEndDate() != null
                    && java.time.LocalDateTime.now().isAfter(competition.getEndDate());
            if (hasEnded) {
                throw new IllegalStateException("Esta competición ha finalizado y ya no acepta votos.");
            } else {
                throw new IllegalStateException("Esta competición está pausada temporalmente. Inténtalo más tarde.");
            }
        }

        long alreadyCastInCategory = voteRepository.countByUserIdAndCategoryId(user.getId(), category.getId());
        if (alreadyCastInCategory >= MAX_VOTES_PER_CATEGORY) {
            throw new IllegalStateException(
                "You already voted for a project in this category.");
        }

        Vote vote = voteFactory.create(user, project, category);
        voteRepository.save(vote);
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
}