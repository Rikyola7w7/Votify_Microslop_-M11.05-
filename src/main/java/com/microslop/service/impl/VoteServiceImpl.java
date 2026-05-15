package com.microslop.service.impl;

import com.microslop.entity.Vote;
import com.microslop.entity.Competition;
import com.microslop.factory.ScaleVoteCreator;
import com.microslop.factory.VoteCreator;
import com.microslop.repository.VoteRepository;
import com.microslop.repository.CategoryRepository;
import com.microslop.repository.CompetitionRepository;
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
    private final VoteCreator voteCreator;
    private final ScaleVoteCreator scaleVoteCreator;
    private final CategoryRepository categoryRepository;
    private final CompetitionRepository competitionRepository;

    public VoteServiceImpl(VoteRepository voteRepository,
                           ProjectService projectService,
                           UserService userService,
                           VoteCreator voteCreator,
                           ScaleVoteCreator scaleVoteCreator,
                           CategoryRepository categoryRepository,
                           CompetitionRepository competitionRepository) {
        this.voteRepository = voteRepository;
        this.projectService = projectService;
        this.userService    = userService;
        this.voteCreator    = voteCreator;
        this.scaleVoteCreator = scaleVoteCreator;
        this.categoryRepository = categoryRepository;
        this.competitionRepository = competitionRepository;
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

        if ("CHECKLIST".equalsIgnoreCase(competition.getVoteType())) {
            throw new IllegalStateException("This competition uses checklist voting. Please use the checklist voting interface.");
        }

        if ("SCALE".equalsIgnoreCase(competition.getVoteType())) {
            throw new IllegalStateException("This competition uses scale voting. Please use the scale voting interface.");
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

        long alreadyCastInCategory = voteRepository.countByUserIdAndCategoryId(user.getId(), category.getId());
        if (alreadyCastInCategory >= MAX_VOTES_PER_CATEGORY) {
            throw new IllegalStateException(
                "You already voted for a project in this category.");
        }

        Vote vote = voteCreator.create(user, project, category);
        voteRepository.save(vote);
    }

    @Override
    public void submitVote(String userUsername, Long projectId, Long categoryId, int points) {
        var user        = userService.searchByUsernameIgnoreCase(userUsername)
                            .orElseThrow(() -> new IllegalStateException("User not found."));
        var project     = projectService.getById(projectId);
        var competition = project.getCompetition();
        var category    = categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new IllegalStateException("Category not found."));

        if ("CHECKLIST".equalsIgnoreCase(competition.getVoteType())) {
            throw new IllegalStateException("This competition uses checklist voting. Please use the checklist voting interface.");
        }

        if ("SCALE".equalsIgnoreCase(competition.getVoteType())) {
            throw new IllegalStateException("This competition uses scale voting. Please use the scale voting interface.");
        }

        if (!competition.isActive()) {
            throw new IllegalStateException("Competition is not active.");
        }

        long alreadyCastInCategory = voteRepository.countByUserIdAndCategoryId(user.getId(), category.getId());
        if (alreadyCastInCategory >= MAX_VOTES_PER_CATEGORY) {
            throw new IllegalStateException(
                "You already voted for a project in this category.");
        }

        Vote vote = new Vote(user, project, category, points);
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

    @Override
    @Transactional(readOnly = true)
    public long countPointsByUserAndCategory(Long userId, Long categoryId) {
        return voteRepository.sumPointsByUserIdAndCategoryId(userId, categoryId);
    }

    // ── Scale Voting ──────────────────────────────────────────────────────

    @Override
    public void submitScaleVote(String userUsername, Long projectId, Long categoryId, int score) {
        var user        = userService.searchByUsernameIgnoreCase(userUsername)
                            .orElseThrow(() -> new IllegalStateException("User not found."));
        var project     = projectService.getById(projectId);
        var competition = project.getCompetition();
        var category    = categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new IllegalStateException("Category not found."));

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

        long alreadyCastInCategory = voteRepository.countByUserIdAndCategoryId(user.getId(), category.getId());
        if (alreadyCastInCategory >= MAX_VOTES_PER_CATEGORY) {
            throw new IllegalStateException("You already voted for a project in this category.");
        }

        Vote vote = scaleVoteCreator.create(user, project, category, score, competition);
        voteRepository.save(vote);
    }

    @Override
    @Transactional(readOnly = true)
    public double getAverageScoreByProject(Long projectId) {
        return voteRepository.avgScoreByProjectId(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public double getAverageScoreByProjectAndCategory(Long projectId, Long categoryId) {
        return voteRepository.avgScoreByProjectIdAndCategoryId(projectId, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getSumScoreByProject(Long projectId) {
        return voteRepository.sumScoreByProjectId(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getSumScoreByProjectAndCategory(Long projectId, Long categoryId) {
        return voteRepository.sumScoreByProjectIdAndCategoryId(projectId, categoryId);
    }
}