package com.microslop.service.impl;

import com.microslop.entity.Vote;
import com.microslop.factory.VoteFactory;
import com.microslop.repository.VoteRepository;
import com.microslop.service.ProjectService;
import com.microslop.service.UserService;
import com.microslop.service.VoteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VoteServiceImpl implements VoteService {

    private static final int MAX_VOTES_PER_COMPETITION = 1;

    private final VoteRepository voteRepository;
    private final ProjectService projectService;
    private final UserService    userService;
    private final VoteFactory    voteFactory;

    public VoteServiceImpl(VoteRepository voteRepository,
                           ProjectService projectService,
                           UserService userService,
                           VoteFactory voteFactory) {
        this.voteRepository = voteRepository;
        this.projectService = projectService;
        this.userService    = userService;
        this.voteFactory    = voteFactory;
    }

    // ── Write ─────────────────────────────────────────────────────────────

    @Override
    public void submitVote(String userUsername, Long projectId) {
        var user        = userService.searchByUsernameIgnoreCase(userUsername)
                            .orElseThrow(() -> new IllegalStateException("User not found."));
        var project     = projectService.getById(projectId);
        var competition = project.getCompetition();

        if (!competition.isActive()) {
            throw new IllegalStateException("Competition is not active.");
        }

        long alreadyCast = voteRepository.countByUserInCompetition(user.getId(), competition.getId());
        if (alreadyCast >= MAX_VOTES_PER_COMPETITION) {
            throw new IllegalStateException(
                "You already voted for a project in this competition.");
        }

        Vote vote = voteFactory.create(user, project);
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
    public long countVotesByUserAndProject(Long userId, Long projectId) {
        return voteRepository.countByUserIdAndProjectId(userId, projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countVotesPerUserInCompetition(Long userId, Long competitionId) {
        return voteRepository.countByUserInCompetition(userId, competitionId);
    }
}