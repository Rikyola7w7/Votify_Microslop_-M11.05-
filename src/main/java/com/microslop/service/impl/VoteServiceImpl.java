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

    private final VoteRepository     voteRepository;
    private final ProjectService     projectService;
    private final UserService        userService;
    private final VoteFactory        voteFactory;

    public VoteServiceImpl(VoteRepository voteRepository,
                       ProjectService projectService,
                       UserService userService,
                       VoteFactory voteFactory) {
        this.voteRepository  = voteRepository;
        this.projectService  = projectService;
        this.userService     = userService;
        this.voteFactory     = voteFactory;
    }

    // ── Write ────────────────────────────────────────────────────────────

    @Override
    public Vote submitVote(String userUsername, Long projectId) {
        var user         = userService.searchByUsername(userUsername).orElseThrow(() -> 
            new IllegalStateException("User not found."));
        var project      = projectService.getById(projectId);
        var competition  = project.getCompetition();
        var username     = user.getUsername();
        if (!competition.isActive()) {
            throw new IllegalStateException("Competition is not active.");
        }
        if (voteRepository.existsByUserUsernameAndProjectId(username, projectId)) {
            throw new IllegalStateException(
                "User '" + user.getName() + "' has already voted for this project.");
        }

        Vote vote = voteFactory.create(user, project);
        return voteRepository.save(vote);
    }

    // ── Read ──────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public long countVotesByProject(Long projectId) {
        return voteRepository.countByProjectId(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserVoted(String userUsername, Long projectId) {
        return voteRepository.existsByUserUsernameAndProjectId(userUsername, projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countVotesPerUserInCompetition(String userId, Long competitionId) {
        return voteRepository.countByUserInCompetition(userId, competitionId);
    }
}
